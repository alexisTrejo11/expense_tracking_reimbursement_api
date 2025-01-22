package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.NotificationMapper;
import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.NotificationRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Implementations.EmailServiceImpl;
import alexisTrejo.expenses.tracking.api.Service.Implementations.NotificationServiceImpl;
import alexisTrejo.expenses.tracking.api.Utils.MessageGenerator;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EmailServiceImpl emailServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private MessageGenerator message;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private Notification testNotification;
    private NotificationDTO testNotificationDTO;
    private NotificationInsertDTO testInsertDTO;
    private User testUser;
    private final Long NOTIFICATION_ID = 1L;
    private final Long USER_ID = 1L;

    @BeforeEach
    void setUp() {
        testNotification = new Notification();
        testNotification.setId(NOTIFICATION_ID);
        testNotification.setMessage("Test message");
        testNotification.setUser(new User());

        testNotificationDTO = new NotificationDTO();
        testNotificationDTO.setId(NOTIFICATION_ID);
        testNotificationDTO.setMessage("Test message");

        testInsertDTO = new NotificationInsertDTO();
        testInsertDTO.setUserId(USER_ID);
        testInsertDTO.setMessage("Test notification");

        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setEmail("test@example.com");
    }

    @Test
    void getNotificationByUserId_ShouldReturnPageOfNotifications() {
        Pageable pageable = mock(Pageable.class);
        Page<Notification> notificationPage = new PageImpl<>(Collections.singletonList(testNotification));

        when(notificationRepository.findByUser_Id(USER_ID, pageable)).thenReturn(notificationPage);
        when(notificationMapper.entityToDTO(testNotification)).thenReturn(testNotificationDTO);

        Page<NotificationDTO> result = notificationService.getNotificationByUserId(USER_ID, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
        verify(notificationRepository).findByUser_Id(USER_ID, pageable);
    }

    @Test
    void getNotificationById_WhenNotificationExists_ShouldReturnNotificationDTO() {
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(testNotification));
        when(notificationMapper.entityToDTO(testNotification)).thenReturn(testNotificationDTO);

        NotificationDTO result = notificationService.getNotificationById(NOTIFICATION_ID);

        assertNotNull(result);
        assertEquals(NOTIFICATION_ID, result.getId());
        verify(notificationRepository).findById(NOTIFICATION_ID);
    }

    @Test
    void getNotificationById_WhenNotificationDoesNotExist_ShouldReturnNull() {
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.empty());

        NotificationDTO result = notificationService.getNotificationById(NOTIFICATION_ID);

        assertNull(result);
        verify(notificationRepository).findById(NOTIFICATION_ID);
    }

    @Test
    void createNotification_ShouldSaveNotification() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        when(notificationMapper.insertDtoToEntity(testInsertDTO)).thenReturn(testNotification);

        notificationService.createNotification(testInsertDTO);

        verify(notificationRepository).save(testNotification);
    }

    @Test
    void createNotification_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.createNotification(testInsertDTO));
    }

    @Test
    void sendNotificationFromExpense_ShouldSendEmailAndSaveNotification() {
        ExpenseDTO expenseDTO = new ExpenseDTO();
        expenseDTO.setUserId(USER_ID);
        expenseDTO.setStatus(ExpenseStatus.APPROVED);
        expenseDTO.setDate(LocalDate.now());

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(testUser));
        Notification notification = mock(Notification.class);
        when(notificationMapper.insertDtoToEntity(any())).thenReturn(notification);
        when(notificationRepository.saveAndFlush(any())).thenReturn(notification);

        notificationService.sendNotificationFromExpense(expenseDTO);

        verify(emailServiceImpl).sendEmailFromNotification(notification);
        verify(notificationRepository).saveAndFlush(notification);
    }

    @Test
    void markNotificationAsRead_ShouldMarkAsRead() {
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.of(testNotification));

        notificationService.markNotificationAsRead(NOTIFICATION_ID);

        assertTrue(testNotification.getRead());
        verify(notificationRepository).save(testNotification);
    }

    @Test
    void markNotificationAsRead_WhenNotificationNotFound_ShouldThrowException() {
        when(notificationRepository.findById(NOTIFICATION_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> notificationService.markNotificationAsRead(NOTIFICATION_ID));
    }
}
