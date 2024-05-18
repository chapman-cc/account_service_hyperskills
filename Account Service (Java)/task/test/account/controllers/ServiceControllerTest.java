package account.controllers;

import account.dtos.EmployeeDTO;
import account.exceptions.AdminDeletionException;
import account.exceptions.EmployeeNotFoundException;
import account.models.Employee;
import account.requestBodies.UpdateRoleRequest;
import account.responses.RemoveEmployeeResponse;
import account.services.EmployeeService;
import account.utils.EmployeeFaker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ServiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeFaker faker;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;


    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void cannotAccessUnAuthorized() throws Exception {
        ResultMatcher[] unauthorizedMatcher = getResultMatcher("Somehow is unauthorized", HttpStatus.UNAUTHORIZED);
        mockMvc.perform(put("/api/admin/user/role"))
                .andExpectAll(status().isUnauthorized());
        mockMvc.perform(delete("/api/admin/user"))
                .andExpectAll(status().isUnauthorized());
        mockMvc.perform(get("/api/admin/user"))
                .andExpectAll(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"ACCOUNTANT", "USER"})
    void cannotAccessWithUserRole() throws Exception {
        ResultMatcher[] forbiddenMatcher = getResultMatcher("Access Denied", HttpStatus.FORBIDDEN);
        mockMvc.perform(put("/api/admin/user/role"))
                .andExpectAll(forbiddenMatcher);
        mockMvc.perform(delete("/api/admin/user"))
                .andExpectAll(forbiddenMatcher);
        mockMvc.perform(get("/api/admin/user"))
                .andExpectAll(forbiddenMatcher);
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void canGetEmployeeListOrderById() throws Exception {

        List<EmployeeDTO> employeeDTOList = Stream.generate(() -> faker.generateEmployee())
                .limit(10)
                .map(e -> modelMapper.map(e, EmployeeDTO.class))
                .toList();

        when(employeeService.getAllEmployee()).thenReturn(employeeDTOList);

        String content = mockMvc.perform(get("/api/admin/user"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<Employee> parsed = new ObjectMapper().readValue(content, new TypeReference<List<Employee>>() {
        });


        assertThat(parsed)
                .isNotEmpty()
                .allSatisfy(employee -> {
                    assertThat(employee.getId()).isNotNull();
                    assertThat(employee.getName()).isNotNull();
                    assertThat(employee.getLastname()).isNotNull();
                    assertThat(employee.getEmail()).isNotNull();
                    assertThat(employee.getRoles()).isNotNull();
                    assertThat(employee.getRoles()).isNotEmpty();
                });
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void canDeleteEmployee() throws Exception {
        Employee employee = faker.generateEmployee();

        when(employeeService.removeEmployee(employee.getEmail())).thenReturn(new RemoveEmployeeResponse(employee.getEmail()));

        String path = "/api/admin/user/%s".formatted(employee.getEmail());
        mockMvc.perform(delete(path))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.user").value(employee.getEmail()),
                        jsonPath("$.status").value("Deleted successfully!")
                );

        verify(employeeService).removeEmployee(employee.getEmail());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void cannotDeleteIfNotValidEmail() throws Exception {
        String path = "/api/admin/user/%s".formatted("gibberish");
        mockMvc.perform(delete(path))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void cannotDeleteEmployeeIfNotFound() throws Exception {
        Employee employee = faker.generateEmployee();

        when(employeeService.removeEmployee(employee.getEmail())).thenThrow(new EmployeeNotFoundException());

        String path = "/api/admin/user/%s".formatted(employee.getEmail());
        mockMvc.perform(delete(path))
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.timestamp").isString(),
                        jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()),
                        jsonPath("$.error").value(HttpStatus.NOT_FOUND.getReasonPhrase()),
                        jsonPath("$.message").value("User not found!"),
                        jsonPath("$.path").value(path)
                );
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void cannotDeleteEmployeeIfIsAdmin() throws Exception {
        Employee employee = faker.generateEmployee();

        when(employeeService.removeEmployee(employee.getEmail())).thenThrow(new AdminDeletionException());

        String path = "/api/admin/user/%s".formatted(employee.getEmail());
        mockMvc.perform(delete(path))
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.timestamp").isString(),
                        jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()),
                        jsonPath("$.error").value(HttpStatus.BAD_REQUEST.getReasonPhrase()),
                        jsonPath("$.message").value("Can't remove ADMINISTRATOR role!"),
                        jsonPath("$.path").value(path)
                );
    }

    @Test
    @WithMockUser(roles = {"ADMINISTRATOR"})
    void canUpdateRole() throws Exception {
        Employee employee = faker.generateEmployeeWithId();
        String newRole = "SOME_RANDOM_NEW_ROLE";
        employee.addRole(newRole);
        UpdateRoleRequest requestBody = UpdateRoleRequest.builder()
                .user(employee.getEmail())
                .role(newRole)
                .operation("GRANT")
                .build();

        when(employeeService.updateRole(requestBody)).thenReturn(modelMapper.map(employee, EmployeeDTO.class));

        String path = "/api/admin/user/role";
        String content = mockMvc.perform(
                        put(path)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestBody)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(employee.getId()),
                        jsonPath("$.name").value(employee.getName()),
                        jsonPath("$.lastname").value(employee.getLastname()),
                        jsonPath("$.email").value(employee.getEmail()),
                        jsonPath("$.roles").exists()
                )
                .andReturn()
                .getResponse()
                .getContentAsString();

        EmployeeDTO parsed = objectMapper.readValue(content, EmployeeDTO.class);

        assertThat(parsed.getRoles()).contains(newRole);
    }


    ResultMatcher[] getResultMatcher(String message, HttpStatus status) {
        ResultMatcher statusMatcher = switch (status) {
            case FORBIDDEN -> status().isForbidden();
            case UNAUTHORIZED -> status().isUnauthorized();
            case NOT_FOUND -> status().isNotFound();
            case BAD_REQUEST -> status().isBadRequest();
            case CONFLICT -> status().isConflict();
            default -> status().isOk();
        };
        return new ResultMatcher[]{
                statusMatcher,
                jsonPath("$.timestamp").isString(),
                jsonPath("$.status").value(status.value()),
                jsonPath("$.error").value(status.getReasonPhrase()),
                jsonPath("$.message").value(message),
                jsonPath("$.path").isString()
        };
    }
}