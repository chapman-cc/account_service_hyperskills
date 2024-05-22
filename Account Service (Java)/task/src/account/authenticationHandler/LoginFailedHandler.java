package account.authenticationHandler;

import account.exceptions.EmployeeNotFoundException;
import account.models.Employee;
import account.services.EmployeeService;
import account.services.SecurityEventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class LoginFailedHandler implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    private final EmployeeService employeeService;
    private final SecurityEventService securityEventService;
    private final HttpServletRequest request;

    public LoginFailedHandler(EmployeeService employeeService, SecurityEventService securityEventService, HttpServletRequest request) {
        this.employeeService = employeeService;
        this.securityEventService = securityEventService;
        this.request = request;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        Authentication authentication = event.getAuthentication();
        String principal = (String) authentication.getPrincipal();
//        String credentials = (String) authentication.getCredentials();
//        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
//        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String path = request.getRequestURI();
        employeeService.updateLoginAttempts(principal);
        logSecurityEvent(principal, path);
    }

    private void logSecurityEvent(String principal, String path) {
        Employee employee = employeeService.findByEmail(principal).orElseThrow(EmployeeNotFoundException::new);
        if (employee.getLoginInformation().isLocked()) {
            securityEventService.logLockUser(principal, principal, path);
        }
        if (employee.getLoginInformation().getLoginAttempts() == 0) {
            securityEventService.logLoginFailed(principal, principal, path);
        }
        if (employee.getLoginInformation().getLoginAttempts() > 0) {
            securityEventService.logBruteForce(principal, principal, path);
        }
    }
}

