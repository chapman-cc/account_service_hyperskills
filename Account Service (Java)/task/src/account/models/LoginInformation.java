package account.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "login_information")
public class LoginInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_locked")
    private boolean isLocked;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "login_attempts")
    private int loginAttempts;

    {
        isLocked = false;
        isEnabled = true;
        loginAttempts = 0;
    }

    public void increaseLoginAttempts() {
        loginAttempts++;
    }

    public void resetLoginAttempts() {
        loginAttempts = 0;
    }
}
