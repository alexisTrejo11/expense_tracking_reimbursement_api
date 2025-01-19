package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.NotificationMapper;
import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.NotificationRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.DomainService.NotificationDomainService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailServiceImpl emailServiceImpl;
    private final NotificationDomainService notificationDomainService;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   EmailServiceImpl emailServiceImpl,
                                   NotificationDomainService notificationDomainService,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.emailServiceImpl = emailServiceImpl;
        this.notificationDomainService = notificationDomainService;
        this.userRepository = userRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public Result<Page<NotificationDTO>> getNotificationByUserId(Long userId, Pageable pageable) {
        boolean isUserExisting = userRepository.existsById(userId);
        if (!isUserExisting) {
            return Result.error("User With Id(" + userId + ") Not Found");
        }

        Page<Notification> notificationPage =  notificationRepository.findByUser_Id(userId, pageable);
        Page<NotificationDTO> notificationDTOPage = notificationPage.map(notificationMapper::entityToDTO);
        return Result.success(notificationDTOPage);
    }

    @Override
    public Result<NotificationDTO> getNotificationById(Long notificationId) {
        Optional<Notification> optionalNotification =  notificationRepository.findById(notificationId);
        return optionalNotification
                .map(notification -> Result.success(notificationMapper.entityToDTO(notification)))
                .orElseGet(() -> Result.error("Notification With Id(" + notificationId + ") Not Found"));

    }


    @Override
    @Transactional
    public void createNotification(NotificationInsertDTO notificationInsertDTO) {
        boolean isUserExisting = userRepository.existsById(notificationInsertDTO.getUserId());
        if (!isUserExisting) {
            throw new RuntimeException("User Not Found");
        }

        Notification notification = notificationMapper.insertDtoToEntity(notificationInsertDTO);
        notification.setUser(new User(notificationInsertDTO.getUserId()));

        notificationRepository.saveAndFlush(notification);
    }

    @Transactional
    @Override
    @Async("taskExecutor")
    public void sendNotificationFromExpense(ExpenseDTO expenseDTO) {
        Notification notification = notificationDomainService.createNotificationFromExpense(expenseDTO);
        log.info("Notification Successfully created with Id {} for User Id {}", notification.getId(), notification.getUser().getId());

        emailServiceImpl.sendEmailFromNotification(notification);
        log.info("Email Successfully Send It To The Email From User Id {}", notification.getUser().getId());
    }
    
    @Override
    public Result<Void> markNotificationAsRead(Long notificationId) {
        Optional<Notification> optionalNotification =  notificationRepository.findById(notificationId);
        return optionalNotification
                .map(notification -> {
                    notification.setAsRead();
                    notificationRepository.saveAndFlush(notification);
                    return Result.success();
                })
                .orElseGet(() -> Result.error("Notification With Id(" + notificationId + ") Not Found"));
    }

}
