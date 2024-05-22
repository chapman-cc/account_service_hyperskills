package account.controllers;

import account.models.SecurityEvent;
import account.services.SecurityEventService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SecurityEventService securityEventService;


    @Test
    @WithMockUser(roles = "USER")
    void cannotGetSecurityEventsByUser() throws Exception {
        SecurityEvent event = new SecurityEvent("ACCESS_DENIED", "john@acme.com", "john@acme.com", "/api/random/path");
        when(securityEventService.getSecurityEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/api/security/events"))
                .andExpect(status().isForbidden());

    }
    @Test
    @WithMockUser(roles = "AUDITOR")
    void canGetSecurityEventsByAuditor() throws Exception {
        SecurityEvent event = new SecurityEvent("ACCESS_DENIED", "john@acme.com", "john@acme.com", "/api/random/path");
        when(securityEventService.getSecurityEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/api/security/events"))
                .andExpect(status().isOk());

    }
}