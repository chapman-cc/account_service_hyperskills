package account.authenticationHandler;

import account.services.EmployeeService;
import account.services.SecurityEventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginDeniedHandler implements ApplicationListener<AuthorizationDeniedEvent> {
    private final EmployeeService employeeService;
    private final SecurityEventService securityEventService;
    private final HttpServletRequest request;

    @Autowired
    public LoginDeniedHandler(EmployeeService employeeService, SecurityEventService securityEventService, HttpServletRequest request) {
        this.employeeService = employeeService;
        this.securityEventService = securityEventService;
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthorizationDeniedEvent event) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String principal = authentication.getPrincipal() instanceof UserDetails
                ? ((UserDetails) authentication.getPrincipal()).getUsername()
                : (String) authentication.getPrincipal();

        securityEventService.logAccessDenied(principal, principal, request.getRequestURI());
    }
}
