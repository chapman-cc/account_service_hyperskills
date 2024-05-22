package account.aop;

import account.models.Employee;
import account.requestBodies.UpdateRoleRequest;
import account.requestBodies.UserLockOperation;
import account.services.SecurityEventService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class InfoSecurityAspect {
    private final SecurityEventService service;
    private final HttpServletRequest request;

    public InfoSecurityAspect(SecurityEventService service, HttpServletRequest request) {
        this.service = service;
        this.request = request;
    }

    @Pointcut("execution(* account.controllers.AuthenticationController.signUp(..))")
    public void forSignup() {
    }

    @Pointcut("execution(* account.controllers.AuthenticationController.changePassword(..))")
    public void forChangePassword() {
    }

    @Pointcut("execution(* account.controllers.ServiceController.changeRole(..))")
    public void forChangeRole() {
    }

    @Pointcut("execution(* account.controllers.ServiceController.deleteUser(..))")
    public void forDeleteUser() {
    }

    @Pointcut("execution(* account.controllers.ServiceController.updateUserLockStatus(..))")
    public void forUpdateUserLockStatus() {
    }

    /*
     * AUTHENTICATION CONTROLLER
     * */
    @AfterReturning("forSignup()")
    public void logSignUp(JoinPoint joinPoint) {

        Employee arg = (Employee) joinPoint.getArgs()[0];
        String path = request.getRequestURI();
        service.logCreateUser("Anonymous", arg.getEmail(), path);
    }

    @AfterReturning("forChangePassword()")
    public void logChangePassword(JoinPoint joinPoint) {
        UserDetails userDetails = (UserDetails) joinPoint.getArgs()[0];
        service.logChangePassword(userDetails.getUsername(), userDetails.getUsername(), request.getRequestURI());
    }

    @AfterReturning("forChangeRole()")
    public void logChangeRole(JoinPoint joinPoint) {
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = principal.getUsername();
        String path = request.getRequestURI();
        UpdateRoleRequest arg = (UpdateRoleRequest) joinPoint.getArgs()[0];
        if (arg.getOperation().equals("GRANT")) {
            service.logGrantRole(email, arg.getUser(), path);
        }
        if (arg.getOperation().equals("REMOVE")) {
            service.logRemoveRole(email, arg.getUser(), path);
        }
    }

    @AfterReturning("forDeleteUser()")
    public void logDeleteUser(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        HttpServletRequest req = (HttpServletRequest) args[0];
        UserDetails userDetails = (UserDetails) args[1];
        String arg = (String) args[2];
        service.logDeleteUser(userDetails.getUsername(), arg, req.getRequestURI());
    }

    @AfterReturning("forUpdateUserLockStatus()")
    public void logUpdateUserLockStatus(JoinPoint joinPoint) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();
        String path = request.getRequestURI();
        UserLockOperation arg = (UserLockOperation) joinPoint.getArgs()[0];
        if (arg.getOperation().equals("LOCK")) {
            service.logLockUser(email, arg.getUser(), path);
        }

        if (arg.getOperation().equals("UNLOCK")) {
            service.logUnlockUser(email, arg.getUser(), path);
        }
    }
}
