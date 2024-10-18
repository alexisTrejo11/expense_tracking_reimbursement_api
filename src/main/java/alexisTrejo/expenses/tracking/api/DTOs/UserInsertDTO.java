package alexisTrejo.expenses.tracking.api.DTOs;

import alexisTrejo.expenses.tracking.api.Models.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class UserInsertDTO {

    @JsonProperty("email")
    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @JsonProperty("first_name")
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @JsonProperty("last_name")
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @JsonProperty("department")
    @NotBlank(message = "Department is required")
    @Size(max = 100, message = "Department must not exceed 100 characters")
    private String department;
}
