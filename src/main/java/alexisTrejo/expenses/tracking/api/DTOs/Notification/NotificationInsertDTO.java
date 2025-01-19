package alexisTrejo.expenses.tracking.api.DTOs.Notification;

import alexisTrejo.expenses.tracking.api.Utils.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationInsertDTO {

    @JsonProperty("user_id")
    @NotNull(message = "user_id is obligatory")
    @Positive(message = "user_id must be positive")
    private Long userId;

    @Enumerated(EnumType.ORDINAL)
    @JsonProperty("type")
    @NotNull(message = "type is obligatory")
    private NotificationType type;


    @JsonProperty("message")
    @NotNull(message = "message is obligatory")
    @NotEmpty(message = "message can't be empty")
    private String message;
}
