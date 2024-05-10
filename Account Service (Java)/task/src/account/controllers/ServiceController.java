package account.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/user")
public class ServiceController {

    @PutMapping("/role")
    public void changeRole() {

    }

    @DeleteMapping
    public void deleteUser(){

    }

    @GetMapping
    public void getUsers(){

    }
}
