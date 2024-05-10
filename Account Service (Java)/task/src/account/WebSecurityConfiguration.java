package account;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class WebSecurityConfiguration {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/hello-world").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/actuator/shutdown").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();

//        return http
////                .headers().frameOptions().disable()
////                .and().csrf(AbstractHttpConfigurer::disable)
//
//                .httpBasic(Customizer.withDefaults())
//                .authorizeHttpRequests(authorize -> authorize
//                                .requestMatchers("/h2-console").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/api/auth/signup").permitAll()
//                                .anyRequest().authenticated()
////                        .requestMatchers(HttpMethod.GET, "/api/empl/payment").hasAnyRole("USER", "ACCOUNTANT")
////                        .requestMatchers(HttpMethod.POST, "/api/acct/payments").hasRole("ACCOUNTANT")
////                        .requestMatchers(HttpMethod.PUT, "/api/acct/payments").hasRole("ACCOUNTANT")
////
////                        .requestMatchers( "/api/admin/user").hasRole("ADMIN")
//
//                )
//                .formLogin(Customizer.withDefaults())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .build();
    }
}
