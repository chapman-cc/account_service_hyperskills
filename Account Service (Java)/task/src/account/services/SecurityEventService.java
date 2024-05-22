package account.services;

import account.models.SecurityEvent;
import account.repositories.SecurityEventRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecurityEventService {
    private final SecurityEventRepository securityEventRepository;

    public SecurityEventService(SecurityEventRepository securityEventRepository) {
        this.securityEventRepository = securityEventRepository;
    }

    public List<SecurityEvent> getSecurityEvents() {
        return securityEventRepository.findAll();
    }

    /* A user has been successfully registered */
    public void logCreateUser(String subject, String object, String path) {
        addEvent("CREATE_USER", subject, object, path);
    }

    /* A user has changed the password successfully */
    public void logChangePassword(String subject, String object, String path) {
        addEvent("CHANGE_PASSWORD", subject, object, path);
    }

    /* A user is trying to access a resource without access rights */
    public void logAccessDenied(String subject, String object, String path) {
        addEvent("ACCESS_DENIED", subject, object, path);
    }

    /* Failed authentication */
    public void logLoginFailed(String subject, String object, String path) {
        addEvent("LOGIN_FAILED", subject, object, path);
    }

    /* A role is granted to a user */
    public void logGrantRole(String subject, String object, String path) {
        addEvent("GRANT_ROLE", subject, object, path);
    }

    /* A role has been revoked */
    public void logRemoveRole(String subject, String object, String path) {
        addEvent("REMOVE_ROLE", subject, object, path);
    }
    /* The Administrator has locked the user */
    public void logLockUser(String subject, String object, String path) {
        addEvent("LOCK_USER", subject, object, path);
    }

    /* The Administrator has unlocked a user */
    public void logUnlockUser(String subject, String object, String path) {
        addEvent("UNLOCK_USER", subject, object, path);
    }
    /* The Administrator has deleted a user	*/
    public void logDeleteUser(String subject, String object, String path) {
        addEvent("DELETE_USER", subject, object, path);
    }
    /* A user has been blocked on suspicion of a brute force attack */
    public void logBruteForce(String subject, String object, String path) {
        addEvent("BRUTE_FORCE", subject, object, path);
    }


    public void addEvent(String action, String subject, String object, String path) {
        SecurityEvent event = new SecurityEvent(action, subject, object, path);
        addEvent(event);
    }

    public void addEvent(SecurityEvent event) {
        securityEventRepository.save(event);
    }

}
