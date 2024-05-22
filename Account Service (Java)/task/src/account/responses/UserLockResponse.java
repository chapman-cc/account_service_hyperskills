package account.responses;


import lombok.Data;

public record UserLockResponse (String status) {


    public UserLockResponse(String user, boolean locked) {
        this("User %s %s!".formatted(user, locked ? "locked" : "unlocked"));
    }
}
