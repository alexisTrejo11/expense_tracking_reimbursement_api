package alexisTrejo.expenses.tracking.api.Service.DomainService;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Models.enums.NotificationType;
import alexisTrejo.expenses.tracking.api.Repository.NotificationRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
public class NotificationDomainService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public NotificationDomainService(NotificationRepository notificationRepository,
                                     UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification createNotificationFromExpense(ExpenseDTO expenseDTO) {
        NotificationType notificationType = mapExpenseStatusToNotificationType(expenseDTO.getStatus());
        String notificationMessage = generateNotificationMessage(expenseDTO);

        User user = userRepository.findById(expenseDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));


        Notification notification = Notification.builder()
                .createdAt(LocalDateTime.now())
                .type(notificationType)
                .read(Boolean.FALSE)
                .user(user)
                .message(notificationMessage)
                .build();

        notificationRepository.saveAndFlush(notification);

        return notification;
    }

    private NotificationType mapExpenseStatusToNotificationType(ExpenseStatus status) {
        return switch (status) {
            case PENDING -> NotificationType.EXPENSE_REJECTION;
            case APPROVED -> NotificationType.EXPENSE_APPROVAL;
            case REIMBURSED -> NotificationType.REIMBURSEMENT_COMPLETED;
            default -> throw new IllegalArgumentException("Unexpected status: " + status);
        };
    }

    private String generateNotificationMessage(ExpenseDTO expenseDTO) {
        StringBuilder notificationMessage = new StringBuilder();
        ExpenseStatus status = expenseDTO.getStatus();

        notificationMessage.append("Your Expense Requested At ").append(expenseDTO.getDate());

        switch (status) {
            case PENDING -> notificationMessage.append(" Was Successfully Processed and Will Be Checked For Validation As Soon As Possible.");
            case APPROVED -> notificationMessage.append(" Was Approved.");
            case REIMBURSED -> notificationMessage.append(" Was Reimbursed.");
            case REJECTED -> notificationMessage.append(" Was Rejected. Check The Expense For More Details.");
        }

        return notificationMessage.toString();
    }
}
