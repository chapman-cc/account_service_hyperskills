package account.models;

import account.utils.Regex;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "Lastname is mandatory")
    @Column(name = "lastname")
    private String lastname;

    @Email(message = "Email is not valid", regexp = Regex.EMPLOYEE_EMAIL)
    @NotBlank(message = "Email is mandatory")
    @Column(name = "email")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Length(min = 12, message = "Password is at least 13 letter long")
    @Column(name = "password")
    private String password;

    @Column(name = "role")
    private String role;

    public Employee(String name, String lastname, String email, String password, String role) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
