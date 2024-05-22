package account.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
class BreachedPasswordServiceTest {

    private final BreachedPasswordService breachedPasswordService;

    @Autowired
    public BreachedPasswordServiceTest(BreachedPasswordService breachedPasswordService) {
        this.breachedPasswordService = breachedPasswordService;
    }

    @Test
    void checkIfPasswordIsBreached() {
        String[] breachedPasswords = {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril", "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust", "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};
        List<Boolean> booleans = Arrays.stream(breachedPasswords).map(password -> breachedPasswordService.check(password)).toList();
        assertThat(booleans).containsOnly(true);
    }
}