package account;

import account.responses.HttpErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class WebSecurityConfiguration {
    @Autowired
    private ObjectMapper objectMapper;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(13);
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> {
                    csrf.disable();
                    csrf.ignoringRequestMatchers("/h2-console/**");
                })
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/hello-world").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").permitAll()

                        .requestMatchers("/api/admin/user/**").hasRole("ADMINISTRATOR")

                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
                        .requestMatchers(HttpMethod.POST, "/api/empl/payment").hasRole( "ACCOUNTANT")
                        .requestMatchers(HttpMethod.PUT, "/api/empl/payment").hasRole( "ACCOUNTANT")

                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").permitAll()

                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(getExceptionHandler())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

    }

    private Customizer<ExceptionHandlingConfigurer<HttpSecurity>> getExceptionHandler() {
        return exHandler -> {
            exHandler.accessDeniedHandler((req, res, ex) -> {
                String path = req.getRequestURI();
                res.setStatus(HttpStatus.FORBIDDEN.value());
                res.setContentType(MediaType.APPLICATION_JSON_VALUE);
                res.getWriter().write(objectMapper.writeValueAsString(HttpErrorResponse.forbidden(ex.getMessage(), path)));
            });
        };
    }
}
