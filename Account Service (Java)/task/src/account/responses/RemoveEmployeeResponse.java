package account.responses;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RemoveEmployeeResponse {
    private String status;
    private String user;

    public RemoveEmployeeResponse(String email) {
        this.user = email;
        this.status = "Deleted successfully!";
    }
}
