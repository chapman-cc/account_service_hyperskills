package account.controllers;

import account.models.Employee;
import account.responses.SignupBodyNotValidResponse;
import account.responses.SignupResponse;
import account.responses.UserExistsResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.net.MalformedURLException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:test"})
class AuthenticationControllerTest {

    @LocalServerPort
    private int port;
    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void canRegisterNewUser() {
        String url = "http://localhost:%d/api/auth/signup".formatted(port);
        Employee employee = new Employee("John", "Doe", "john@acme.com", "passwordabcdefghl", "USER");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        ResponseEntity<SignupResponse> responseEntity = restTemplate.postForEntity(url, request, SignupResponse.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
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
        ResponseEntity<UserExistsResponse> response2 = restTemplate.postForEntity(url, request2, UserExistsResponse.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getBody())
                .hasFieldOrPropertyWithValue("error", "Bad Request")
                .hasFieldOrPropertyWithValue("status", 400)
                .hasFieldOrPropertyWithValue("path", new URL(url).getPath());
    }

    @Test
    void willNotAcceptIncorrectEmailDomainName() throws MalformedURLException {
        String url = "http://localhost:%d/api/auth/signup".formatted(port);
        Employee employee = new Employee("mary", "p", "mary@amce.com", "passwordabcdefghl", "USER");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Employee> request = new HttpEntity<>(employee, headers);

        ResponseEntity<SignupBodyNotValidResponse> response = restTemplate.postForEntity(url, request, SignupBodyNotValidResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .hasFieldOrPropertyWithValue("error", "Bad Request")
                .hasFieldOrPropertyWithValue("status", 400)
                .hasFieldOrPropertyWithValue("path", new URL(url).getPath());

    }

    @Test
    @Disabled
    void changePassword() {
    }
}