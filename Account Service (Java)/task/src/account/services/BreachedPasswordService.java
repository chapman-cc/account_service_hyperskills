package account.services;

import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class BreachedPasswordService {
    public static final String[] breachedPasswords = {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

    public final Set<String> breachedPasswordsSet = Set.of(breachedPasswords);

    public boolean check(String password) {
        return breachedPasswordsSet.contains(password);
    }
}
