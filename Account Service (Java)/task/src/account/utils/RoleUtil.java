package account.utils;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoleUtil {
    public final static List<String> ROLES = List.of(
            "ADMINISTRATOR",
            "USER",
            "ACCOUNTANT",
            "AUDITOR"
    );

    public boolean isValidRole(String role) {
        return ROLES.contains(role);
    }
}
