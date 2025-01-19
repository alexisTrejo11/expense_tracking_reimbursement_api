package alexisTrejo.expenses.tracking.api.DTOs.User;

import alexisTrejo.expenses.tracking.api.Utils.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Data
public class UserDTO extends ProfileDTO {
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
