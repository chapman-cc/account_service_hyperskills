package account.controllers;

import account.requestBodies.UpdateRoleRequest;
import account.responses.RemoveEmployeeResponse;
import account.dtos.EmployeeDTO;
import account.services.EmployeeService;
import account.utils.Regex;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/user")
@Validated
public class ServiceController {

    private EmployeeService employeeService;

    @Autowired
    public ServiceController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDTO> getUsers() {
        return employeeService.getAllEmployee();
    }

    @PutMapping("/role")
    public EmployeeDTO changeRole(@Valid @RequestBody UpdateRoleRequest request) {
        return employeeService.updateRole(request);
    }

    @DeleteMapping("/{email}")
    public RemoveEmployeeResponse deleteUser(@PathVariable @Pattern(regexp = Regex.EMPLOYEE_EMAIL)  String email) {
        return employeeService.removeEmployee(email);
    }
}
