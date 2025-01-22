package alexisTrejo.expenses.tracking.api.Service.Controller;

import alexisTrejo.expenses.tracking.api.Auth.JWTService;
import alexisTrejo.expenses.tracking.api.Controller.ReimbursementController;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ReimbursementService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReimbursementControllerTest {

    @Mock
    private ReimbursementService reimbursementService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private JWTService jwtService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ReimbursementController reimbursementController;

    private ReimbursementDTO testReimbursementDTO;
    private ReimbursementInsertDTO testInsertDTO;
    private final Long REIMBURSEMENT_ID = 1L;
    private final Long USER_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        testReimbursementDTO = new ReimbursementDTO();
        testReimbursementDTO.setId(REIMBURSEMENT_ID);

        testInsertDTO = new ReimbursementInsertDTO();
        testInsertDTO.setExpenseId(1L);
    }

    @Test
    void getReimbursementByUserId_ShouldReturnPageOfReimbursements() {
        // Arrange
        int page = 0;
        int size = 10;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<ReimbursementDTO> reimbursements = Arrays.asList(testReimbursementDTO);
        Page<ReimbursementDTO> reimbursementPage = new PageImpl<>(reimbursements);

        when(reimbursementService.getReimbursementByUserId(USER_ID, pageRequest))
                .thenReturn(reimbursementPage);

        // Act
        ResponseEntity<ResponseWrapper<Page<ReimbursementDTO>>> response =
                reimbursementController.getReimbursementByUserId(USER_ID, page, size);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getData().getContent().size());
        verify(reimbursementService).getReimbursementByUserId(USER_ID, pageRequest);
    }

    @Test
    void getReimbursementById_WhenExists_ShouldReturnReimbursement() {
        // Arrange
        when(reimbursementService.getReimbursementById(REIMBURSEMENT_ID))
                .thenReturn(testReimbursementDTO);

        // Act
        ResponseEntity<ResponseWrapper<ReimbursementDTO>> response =
                reimbursementController.getReimbursementById(REIMBURSEMENT_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getData());
        assertEquals(REIMBURSEMENT_ID, response.getBody().getData().getId());
        verify(reimbursementService).getReimbursementById(REIMBURSEMENT_ID);
    }

    @Test
    void getReimbursementById_WhenNotExists_ShouldReturnNotFound() {
        // Arrange
        when(reimbursementService.getReimbursementById(REIMBURSEMENT_ID))
                .thenReturn(null);

        // Act
        ResponseEntity<ResponseWrapper<ReimbursementDTO>> response =
                reimbursementController.getReimbursementById(REIMBURSEMENT_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNull(response.getBody().getData());
        verify(reimbursementService).getReimbursementById(REIMBURSEMENT_ID);
    }

    @Test
    void createReimbursement_WhenValid_ShouldCreateAndReturnCreated() {
        // Arrange
        when(jwtService.getEmailFromRequest(request)).thenReturn(TEST_EMAIL);
        when(reimbursementService.validate(testInsertDTO)).thenReturn(Result.success());
        when(reimbursementService.createReimbursement(testInsertDTO, TEST_EMAIL))
                .thenReturn(testReimbursementDTO);

        // Act
        ResponseEntity<ResponseWrapper<Void>> response =
                reimbursementController.createReimbursement(testInsertDTO, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reimbursementService).validate(testInsertDTO);
        verify(reimbursementService).createReimbursement(testInsertDTO, TEST_EMAIL);
        verify(notificationService).sendNotificationFromExpense(any());
    }

    @Test
    void createReimbursement_WhenInvalid_ShouldReturnBadRequest() {
        // Arrange
        String errorMessage = "Validation failed";
        when(jwtService.getEmailFromRequest(request)).thenReturn(TEST_EMAIL);
        when(reimbursementService.validate(testInsertDTO))
                .thenReturn(Result.error(errorMessage));

        // Act
        ResponseEntity<ResponseWrapper<Void>> response =
                reimbursementController.createReimbursement(testInsertDTO, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(reimbursementService).validate(testInsertDTO);
        verify(reimbursementService, never()).createReimbursement(any(), any());
        verify(notificationService, never()).sendNotificationFromExpense(any());
    }

    @Test
    void createReimbursement_ShouldSendNotification() {
        // Arrange
        when(jwtService.getEmailFromRequest(request)).thenReturn(TEST_EMAIL);
        when(reimbursementService.validate(testInsertDTO)).thenReturn(Result.success());
        when(reimbursementService.createReimbursement(testInsertDTO, TEST_EMAIL))
                .thenReturn(testReimbursementDTO);

        // Act
        reimbursementController.createReimbursement(testInsertDTO, request);

        // Assert
        verify(notificationService).sendNotificationFromExpense(any());
    }
}