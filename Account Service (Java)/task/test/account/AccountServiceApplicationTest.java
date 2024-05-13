package account;

import account.controllers.AuthenticationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AccountServiceApplicationTest {

    private final ApplicationContext applicationContext;

    @Autowired
    public AccountServiceApplicationTest(ApplicationContext applicationContext, AuthenticationController authenticationController) {
        this.applicationContext = applicationContext;

    }

    @Test
    void contextLoads() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBean(AuthenticationController.class)).isNotNull();
    }
}