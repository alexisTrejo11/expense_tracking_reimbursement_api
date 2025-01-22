package alexisTrejo.expenses.tracking.api.Service.Controller;

import alexisTrejo.expenses.tracking.api.Auth.JWTService;
import alexisTrejo.expenses.tracking.api.Controller.EmployeeExpenseController;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeExpenseControllerTest {

    @Mock
    private ExpenseService expenseService;

    @Mock
    private JWTService jwtService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private EmployeeExpenseController employeeExpenseController;

    private ExpenseInsertDTO expenseInsertDTO;
    private ExpenseDTO expenseDTO;
    private HttpServletRequest mockRequest;

    private static final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        expenseInsertDTO = new ExpenseInsertDTO();
        expenseInsertDTO.setAmount(100.0);
        expenseInsertDTO.setDescription("Test expense");

        expenseDTO = new ExpenseDTO();
        expenseDTO.setId(1L);
        expenseDTO.setAmount(100.0);
        expenseDTO.setDescription("Test expense");

        mockRequest = mock(HttpServletRequest.class);
    }

    @Test
    void getMyExpenses_ShouldReturnExpensesPage() {
        // Arrange
        when(jwtService.getEmailFromRequest(mockRequest)).thenReturn(TEST_EMAIL);

        Pageable pageable = PageRequest.of(0, 10);
        Page<ExpenseDTO> expensePage = new PageImpl<>(Collections.singletonList(expenseDTO));
        when(expenseService.getExpenseByUserEmail(TEST_EMAIL, pageable)).thenReturn(expensePage);

        // Act
        ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> response = employeeExpenseController.getMyExpenses(mockRequest, 0, 10);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expensePage, response.getBody().getData());
        verify(jwtService).getEmailFromRequest(mockRequest);
        verify(expenseService).getExpenseByUserEmail(TEST_EMAIL, pageable);
    }

    @Test
    void requestExpense_ShouldCreateExpenseSuccessfully() {
        // Arrange
        when(jwtService.getEmailFromRequest(mockRequest)).thenReturn(TEST_EMAIL);
        when(expenseService.validate(expenseInsertDTO)).thenReturn(Result.success());
        when(expenseService.createExpense(expenseInsertDTO, TEST_EMAIL, ExpenseStatus.PENDING)).thenReturn(expenseDTO);

        // Act
        ResponseEntity<ResponseWrapper<ExpenseDTO>> response = employeeExpenseController.requestExpense(expenseInsertDTO, mockRequest);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expenseDTO, response.getBody().getData());
        verify(jwtService).getEmailFromRequest(mockRequest);
        verify(expenseService).validate(expenseInsertDTO);
        verify(expenseService).createExpense(expenseInsertDTO, TEST_EMAIL, ExpenseStatus.PENDING);
        verify(notificationService).sendNotificationFromExpense(expenseDTO);
    }

    @Test
    void requestExpense_ShouldReturnBadRequestOnInvalidData() {
        // Arrange
        String errorMessage = "Invalid expense data";
        when(jwtService.getEmailFromRequest(mockRequest)).thenReturn(TEST_EMAIL);
        when(expenseService.validate(expenseInsertDTO)).thenReturn(Result.error(errorMessage));

        // Act
        ResponseEntity<ResponseWrapper<ExpenseDTO>> response = employeeExpenseController.requestExpense(expenseInsertDTO, mockRequest);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(jwtService).getEmailFromRequest(mockRequest);
        verify(expenseService).validate(expenseInsertDTO);
        verify(expenseService, never()).createExpense(any(), any(), any());
        verify(notificationService, never()).sendNotificationFromExpense(any());
    }
}

