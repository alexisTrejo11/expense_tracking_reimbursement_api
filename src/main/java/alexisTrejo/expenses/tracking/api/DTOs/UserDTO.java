package alexisTrejo.expenses.tracking.api.DTOs;

import alexisTrejo.expenses.tracking.api.Models.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDTO extends  ProfileDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("password")
    private String password;

    @Enumerated(EnumType.STRING)
    @JsonProperty("role")
    private Role role;

    @JsonProperty("is_Active")
    private Boolean active = true;
}
