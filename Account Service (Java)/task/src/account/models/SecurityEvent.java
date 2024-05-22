package account.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Table(name = "information_security_events")
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "action")
    private String action;

    @Column(name = "subject")
    private String subject;

    @Column(name = "object")
    private String object;

    @Column(name = "path")
    private String path;

    public SecurityEvent(String action, String subject, String object, String path) {
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
        this.date = LocalDate.now();
    }
}
