package alexisTrejo.expenses.tracking.api.Service.Interfaces;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    Page<NotificationDTO> getNotificationByUserId(Long userId, Pageable pageable);
    NotificationDTO getNotificationById(Long notificationId);

    void createNotification(NotificationInsertDTO notificationInsertDTO);
    void sendNotificationFromExpense(ExpenseDTO expenseDTO);

    Result<Void> markNotificationAsRead(Long notificationId);
}
