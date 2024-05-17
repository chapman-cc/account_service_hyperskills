package account.controllers;

import account.dtos.PayrollDTO;
import account.dtos.PayrollRequestBody;
import account.models.Payroll;
import account.services.PayrollService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class BusinessControllerTest {


    @MockBean
    private PayrollService payrollService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    private PayrollDTO payrollDTO;

    private PayrollRequestBody body;

    @BeforeEach
    void setUp() {
        payrollDTO = PayrollDTO.builder()
                .name("John")
                .lastname("Doe")
                .period("May-2021")
                .salary("1234 dollar(s) 56 cent(s)")
                .build();
        body = PayrollRequestBody.builder()
                .employeeEmail("john@acme.com")
                .salary(1000L)
                .period("01-2024")
                .build();
    }

    @Test
    void test() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/hello-world")
                )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Hello World!"));
    }

    @Test
    void canPostPayroll() throws Exception {

        mockMvc.perform(post("/api/acct/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertJsonToString(List.of(body)))
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value("Added successfully!")
                );
    }

    @Test
    void cannotPostPayrollDueToIncorrectFormat() throws Exception {
        Payroll payroll = Payroll.builder().period("13-2024").salary(-1L).build();

        mockMvc.perform(post("/api/acct/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertJsonToString(List.of(payroll)))
                )
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.status").value(400),
                        jsonPath("$.error").value("Bad Request"),
                        jsonPath("$.path").value("/api/acct/payments")
                );

    }

    @Test
    void canPutPayroll() throws Exception {

        when(payrollService.updatePayroll(any(PayrollRequestBody.class))).thenReturn(any(Payroll.class));

        mockMvc.perform(put("/api/acct/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(convertJsonToString(body))
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.status").value("Updated successfully!")
                );
    }

    @Test
    void cannotGetPayrollUnauthenticated() throws Exception {
        URI uri = new URIBuilder("/api/empl/payment").build();

        mockMvc.perform(get(uri))
                .andExpect(status().isBadRequest());

        verify(payrollService, times(0)).getPayroll(anyString(), anyString());
        verify(payrollService, times(0)).getPayroll(anyString());
    }

    @Test
    @WithMockUser(username = "johndoe@acme.com")
    void canGetPayrollAuthenticatedWithPeriod() throws Exception {
        URI uri = new URIBuilder("/api/empl/payment").addParameter("period", "05-2021").build();

        when(payrollService.getPayroll(anyString(), anyString())).thenReturn(payrollDTO);
        when(payrollService.getPayroll(anyString())).thenReturn(List.of(payrollDTO));

        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(content().json(convertJsonToString(payrollDTO)));

        verify(payrollService, times(1)).getPayroll(anyString(), anyString());
        verify(payrollService, times(0)).getPayroll(anyString());
    }

    @Test
    @WithMockUser(username = "johndoe@acme.com")
    void canGetPayrollsAuthenticatedWithoutPeriod() throws Exception {
        URI uri = new URIBuilder("/api/empl/payment").build();

        when(payrollService.getPayroll(anyString(), anyString())).thenReturn(payrollDTO);
        when(payrollService.getPayroll(anyString())).thenReturn(List.of(payrollDTO));

        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(content().json(convertJsonToString(List.of(payrollDTO))));

        verify(payrollService, times(0)).getPayroll(anyString(), anyString());
        verify(payrollService, times(1)).getPayroll(anyString());

    }

    private static String convertJsonToString(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(object);
    }
}