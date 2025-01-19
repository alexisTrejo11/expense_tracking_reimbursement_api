package alexisTrejo.expenses.tracking.api.DTOs.Notification;

import alexisTrejo.expenses.tracking.api.Utils.enums.NotificationType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NotificationDTO {
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String message;

    private Boolean read;
}
