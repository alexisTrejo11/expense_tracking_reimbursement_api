package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.NotificationMapper;
import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.NotificationRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final NotificationMapper notificationMapper;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   UserRepository userRepository,
                                   NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
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


    @Transactional
    @Override
    public void createNotification(NotificationInsertDTO notificationInsertDTO) {
        boolean isUserExisting = userRepository.existsById(notificationInsertDTO.getUserId());
        if (!isUserExisting) {
            throw new RuntimeException("User Not Found");
        }

        Notification notification = notificationMapper.insertDtoToEntity(notificationInsertDTO);
        notification.setUser(new User(notificationInsertDTO.getUserId()));

        notificationRepository.saveAndFlush(notification);
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
