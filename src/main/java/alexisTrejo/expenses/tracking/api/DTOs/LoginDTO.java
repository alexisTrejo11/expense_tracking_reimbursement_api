package alexisTrejo.expenses.tracking.api.DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDTO {
    @JsonProperty("email")
    private String email;

    @JsonProperty("password")
    private String password;
}
