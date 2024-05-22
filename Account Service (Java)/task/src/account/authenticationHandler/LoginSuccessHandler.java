package account.authenticationHandler;

import account.services.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class LoginSuccessHandler implements ApplicationListener<AuthenticationSuccessEvent> {
    private final EmployeeService employeeService;


    @Autowired
    public LoginSuccessHandler(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        UserDetails principal = (UserDetails) event.getAuthentication().getPrincipal();

        employeeService.resetLoginAttempts(principal.getUsername());

    }
}
