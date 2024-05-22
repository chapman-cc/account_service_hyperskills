package account.controllers;

import account.models.SecurityEvent;
import account.services.SecurityEventService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class SecurityController {

    private final SecurityEventService service;

    @Autowired
    public SecurityController(SecurityEventService service) {
        this.service = service;
    }

    @GetMapping("/api/security/events")
    public List<SecurityEvent> getSecurityEvents(HttpServletRequest request) {
        return  service.getSecurityEvents();
    }
}
