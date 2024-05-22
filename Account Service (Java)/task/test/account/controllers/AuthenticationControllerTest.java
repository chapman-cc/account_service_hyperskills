package account.controllers;

import account.models.Employee;
import account.models.LoginInformation;
import account.models.SecurityEvent;
import account.repositories.EmployeeRepository;
import account.repositories.LoginInformationRepository;
import account.repositories.SecurityEventRepository;
import account.requestBodies.NewPasswordRequest;
import account.responses.HttpErrorResponse;
import account.responses.PasswordChangedResponse;
import account.responses.SignupResponse;
import account.services.EmployeeService;
import account.utils.EmployeeFaker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:test")
class AuthenticationControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private LoginInformationRepository  loginInformationRepository;

    @Autowired
    private SecurityEventRepository securityEventRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeFaker faker;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
        loginInformationRepository.deleteAll();
        securityEventRepository.deleteAll();
    }

    @Test
    void canRegisterNewUser() {
        String url = "http://localhost:%d/api/auth/signup".formatted(port);
        Employee employee = faker.generateEmployee();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        ResponseEntity<SignupResponse> response = restTemplate.postForEntity(url, request, SignupResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        SignupResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isGreaterThan(0);
        assertThat(body.getName()).isEqualTo(employee.getName());
        assertThat(body.getLastname()).isEqualTo(employee.getLastname());
        assertThat(body.getEmail()).isEqualTo(employee.getEmail());
        assertThat(body.getRoles()).isEqualTo(List.of("ADMINISTRATOR"));
    }

    @Test
    void willHaveSignupAOPRecord() {
        String url = "http://localhost:%d/api/auth/signup".formatted(port);
        Employee employee = faker.generateEmployee();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        ResponseEntity<SignupResponse> response = restTemplate.postForEntity(url, request, SignupResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        SecurityEvent securityEvent = securityEventRepository.findAll().iterator().next();

        assertThat(securityEvent.getId()).isGreaterThan(0);
        assertThat(securityEvent.getAction()).isEqualTo("CREATE_USER");
        assertThat(securityEvent.getSubject()).isEqualTo("Anonymous");
        assertThat(securityEvent.getObject()).isEqualTo(employee.getEmail());
        assertThat(securityEvent.getPath()).isNotEmpty();
    }

    @Test
    void willThrowExceptionIfDuplicatedEmail() throws MalformedURLException {
        String url = "http://localhost:%d/api/auth/signup".formatted(port);
        Employee employee1 = new Employee("mary", "p", "mary@acme.com", "passwordabcdefghl", "USER");
        Employee employee2 = new Employee("mary", "j", "mary@acme.com", "passwordabcdefghl", "USER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request1 = new HttpEntity<>(employee1, headers);
        HttpEntity<Employee> request2 = new HttpEntity<>(employee2, headers);


        ResponseEntity<Employee> response1 = restTemplate.postForEntity(url, request1, Employee.class);
        ResponseEntity<HttpErrorResponse> response2 = restTemplate.postForEntity(url, request2, HttpErrorResponse.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getBody())
                .hasFieldOrPropertyWithValue("error", "Bad Request")
                .hasFieldOrPropertyWithValue("status", 400)
                .hasFieldOrPropertyWithValue("path", new URL(url).getPath())
                .hasFieldOrPropertyWithValue("message", "User exist!");
    }

    @Test
    void willNotAcceptIncorrectEmailDomainName() throws MalformedURLException {
        String url = "http://localhost:%d/api/auth/signup".formatted(port);
        Employee employee = faker.generateEmployee();
        employee.setEmail("mary@amce.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        ResponseEntity<HttpErrorResponse> response = restTemplate.postForEntity(url, request, HttpErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .hasFieldOrPropertyWithValue("error", "Bad Request")
                .hasFieldOrPropertyWithValue("status", 400)
                .hasFieldOrPropertyWithValue("path", new URL(url).getPath());

    }

    @Test
    void willNotAcceptPasswordOfLength12() throws MalformedURLException {
        String url = "http://localhost:%d/api/auth/signup".formatted(port);
        Employee employee = faker.generateEmployee();
        employee.setPassword("password");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        ResponseEntity<HttpErrorResponse> response = restTemplate.postForEntity(url, request, HttpErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .hasFieldOrPropertyWithValue("error", "Bad Request")
                .hasFieldOrPropertyWithValue("status", 400)
                .hasFieldOrPropertyWithValue("path", new URL(url).getPath());

    }

    @Test
    void canChangePassword() {
        // Arrange
        String url = "http://localhost:%d/api/auth/changepass".formatted(port);
        final String email = "john@acme.com";
        final String originalPassword = "passwordabcdefghl";
        String newPassword = "password12345678";

        Employee employee = new Employee("John", "Doe", email, originalPassword, "USER");
        employeeService.register(employee);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(email, originalPassword);

        NewPasswordRequest body = new NewPasswordRequest(newPassword);
        HttpEntity<NewPasswordRequest> request = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<PasswordChangedResponse> response = restTemplate.postForEntity(url, request, PasswordChangedResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        PasswordChangedResponse responseBody = response.getBody();
        assertThat(responseBody)
                .hasFieldOrPropertyWithValue("status", "The password has been updated successfully")
                .hasFieldOrPropertyWithValue("email", employee.getEmail());
    }

    @Test
    void willHaveChangePasswordRecord() {
        // Arrange
        String url = "http://localhost:%d/api/auth/changepass".formatted(port);
        final String email = "john@acme.com";
        final String originalPassword = "passwordabcdefghl";
        String newPassword = "password12345678";

        Employee employee = new Employee("John", "Doe", email, originalPassword, "USER");
        employeeService.register(employee);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(email, originalPassword);

        NewPasswordRequest body = new NewPasswordRequest(newPassword);
        HttpEntity<NewPasswordRequest> request = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<PasswordChangedResponse> response = restTemplate.postForEntity(url, request, PasswordChangedResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        SecurityEvent securityEvent = securityEventRepository.findAll().iterator().next();

        assertThat(securityEvent.getId()).isGreaterThan(0);
        assertThat(securityEvent.getAction()).isEqualTo("CHANGE_PASSWORD");
        assertThat(securityEvent.getSubject()).isEqualTo(employee.getEmail());
        assertThat(securityEvent.getObject()).isEqualTo(employee.getEmail());
        assertThat(securityEvent.getPath()).isNotEmpty();
    }

    @Test
    void cannotChangePasswordOfLengthLessThan12() {
        // Arrange
        String url = "http://localhost:%d/api/auth/changepass".formatted(port);
        final String email = "john@acme.com";
        final String originalPassword = "passwordabcdefghl";
        String newPassword = "password";

        Employee employee = new Employee("John", "Doe", email, originalPassword, "USER");
        employeeService.register(employee);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(email, originalPassword);

        NewPasswordRequest body = new NewPasswordRequest(newPassword);
        HttpEntity<NewPasswordRequest> request = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<HttpErrorResponse> response = restTemplate.postForEntity(url, request, HttpErrorResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        HttpErrorResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isEqualTo(400);
        assertThat(responseBody.error()).isEqualTo("Bad Request");
        assertThat(responseBody.message()).isEqualTo("Password length must be 12 chars minimum!");
    }

    @Test
    void cannotRegisterWithBreachedPassword() {
        // Arrange
        String url = "http://localhost:%d/api/auth/signup".formatted(port);

        final String password = "PasswordForJanuary";

        Employee employee = faker.generateEmployee();
        employee.setPassword(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        // Act
        ResponseEntity<HttpErrorResponse> response = restTemplate.postForEntity(url, request, HttpErrorResponse.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        HttpErrorResponse responseBody = response.getBody();
        assertThat(responseBody).isNotNull();
        assertThat(responseBody.status()).isEqualTo(400);
        assertThat(responseBody.error()).isEqualTo("Bad Request");
        assertThat(responseBody.message()).isEqualTo("The password is in the hacker's database!");
    }

    @Test
    void willIncreaseLoginFailAttempt() {
        // Arrange
        String url = "http://localhost:%d/api/auth/changepass".formatted(port);
        final String email = "john@acme.com";
        final String password = "passwordabcdefghl";

        Employee administrator = new Employee("John", "Doe", email, password, "USER");
        Employee employee = faker.generateEmployee();

        employeeService.register(administrator);
        employeeService.register(employee);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(employee.getEmail(), "mistyped_password");

        NewPasswordRequest body = new NewPasswordRequest(password);
        HttpEntity<NewPasswordRequest> request = new HttpEntity<>(body, headers);

        ResponseEntity<HttpErrorResponse> response = null;
        // Act
        for (int i = 0; i < 4; i++) {
            response = restTemplate.postForEntity(url, request, HttpErrorResponse.class);
        }

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        Employee found = employeeRepository.findById(employee.getId()).orElseThrow();
        assertThat(found.getLoginInformation().getLoginAttempts()).isEqualTo(4);
        assertThat(found.getLoginInformation().isLocked()).isFalse();
    }

    @Test
    void willLockUserIfLoginFailed() {
        // Arrange
        String url = "http://localhost:%d/api/auth/changepass".formatted(port);
        final String email = "john@acme.com";
        final String password = "passwordabcdefghl";

        Employee administrator = new Employee("John", "Doe", email, password, "USER");
        Employee employee = faker.generateEmployee();
        employeeService.register(administrator);
        employeeService.register(employee);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(employee.getEmail(), "mistyped_password");

        NewPasswordRequest body = new NewPasswordRequest(password);
        HttpEntity<NewPasswordRequest> request = new HttpEntity<>(body, headers);

        ResponseEntity<HttpErrorResponse> response = null;
        // Act
        for (int i = 0; i < 5; i++) {
            response = restTemplate.postForEntity(url, request, HttpErrorResponse.class);
        }

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        Employee found = employeeRepository.findById(employee.getId()).orElseThrow();
        assertThat(found.getLoginInformation().getLoginAttempts()).isGreaterThan(4);
        assertThat(found.getLoginInformation().isLocked()).isTrue();
    }
}