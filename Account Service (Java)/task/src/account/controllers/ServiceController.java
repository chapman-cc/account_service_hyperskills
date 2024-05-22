package account.controllers;

import account.dtos.EmployeeDTO;
import account.requestBodies.UpdateRoleRequest;
import account.requestBodies.UserLockOperation;
import account.responses.RemoveEmployeeResponse;
import account.responses.UserLockResponse;
import account.services.EmployeeService;
import account.utils.Regex;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
@Validated
public class ServiceController {

    private final EmployeeService employeeService;

    @Autowired
    public ServiceController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> getUsers() {
        return employeeService.getAllEmployee();
    }

    @PutMapping("/role")
    public EmployeeDTO changeRole(@Valid @RequestBody UpdateRoleRequest body) {
        return employeeService.updateRole(body);
    }

    @DeleteMapping("/{email}")
    public RemoveEmployeeResponse deleteUser(HttpServletRequest request, @AuthenticationPrincipal UserDetails userDetails, @PathVariable @Pattern(regexp = Regex.EMPLOYEE_EMAIL) String email) {
        return employeeService.removeEmployee(email);
    }

    @PutMapping("/access")
    public UserLockResponse updateUserLockStatus(@Valid @RequestBody UserLockOperation body) {
        return employeeService.updateUserLockStatus(body);
    }
}
