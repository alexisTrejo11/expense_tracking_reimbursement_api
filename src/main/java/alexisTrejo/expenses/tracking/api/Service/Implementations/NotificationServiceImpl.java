package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.NotificationMapper;
import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Utils.MessageGenerator;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Utils.enums.NotificationType;
import alexisTrejo.expenses.tracking.api.Repository.NotificationRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;
    private final MessageGenerator message;


    @Override
    public Page<NotificationDTO> getNotificationByUserId(Long userId, Pageable pageable) {
        Page<Notification> notificationPage =  notificationRepository.findByUser_Id(userId, pageable);

        return notificationPage.map(notificationMapper::entityToDTO);
    }

    @Override
    public NotificationDTO getNotificationById(Long notificationId) {
        Optional<Notification> optionalNotification =  notificationRepository.findById(notificationId);

        return optionalNotification
                .map(notificationMapper::entityToDTO)
                .orElse(null);
    }


    @Override
    @Transactional
    public void createNotification(NotificationInsertDTO notificationInsertDTO) {
        User user = userRepository.findById(notificationInsertDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User Not Found"));

        Notification notification = notificationMapper.insertDtoToEntity(notificationInsertDTO);
        notification.setUser(user);

        notificationRepository.save(notification);
    }

    @Transactional
    @Override
    @Async("taskExecutor")
    public void sendNotificationFromExpense(ExpenseDTO expenseDTO) {
        Notification notification = createNotificationFromExpense(expenseDTO);
        log.info("Notification Successfully created with Id {} for User Id {}", notification.getId(), notification.getUser().getId());

        emailServiceImpl.sendEmailFromNotification(notification);
        log.info("Email Successfully Send It To The Email From User Id {}", notification.getUser().getId());
    }
    
    @Override
    public void markNotificationAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(message.notFoundPlain("Notification", "ID", id)));

        notification.setAsRead();
        notificationRepository.save(notification);
    }


    @Transactional
    private Notification createNotificationFromExpense(ExpenseDTO expenseDTO) {
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
