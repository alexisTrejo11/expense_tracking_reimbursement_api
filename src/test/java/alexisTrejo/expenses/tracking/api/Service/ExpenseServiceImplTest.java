package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseRejectDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ExpenseMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Implementations.ExpenseServiceImpl;
import alexisTrejo.expenses.tracking.api.Service.Implementations.ExpenseSummaryService;
import alexisTrejo.expenses.tracking.api.Utils.MessageGenerator;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseMapper expenseMapper;

    @Mock
    private MessageGenerator message;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseSummaryService expenseSummaryService;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private Expense testExpense;
    private ExpenseDTO testExpenseDTO;
    private User testUser;
    private ExpenseInsertDTO testInsertDTO;
    private final Long EXPENSE_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        testExpense = new Expense();
        testExpense.setId(EXPENSE_ID);
        testExpense.setStatus(ExpenseStatus.PENDING);

        testExpenseDTO = new ExpenseDTO();
        testExpenseDTO.setId(EXPENSE_ID);

        testUser = new User();
        testUser.setEmail(TEST_EMAIL);

        testInsertDTO = new ExpenseInsertDTO();
        testInsertDTO.setAmount(50.0);
        testInsertDTO.setDate(LocalDate.now());
    }

    @Test
    void getExpenseById_WhenExpenseExists_ShouldReturnExpenseDTO() {
        when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(testExpense));
        when(expenseMapper.entityToDTO(testExpense)).thenReturn(testExpenseDTO);

        ExpenseDTO result = expenseService.getExpenseById(EXPENSE_ID);

        assertNotNull(result);
        assertEquals(EXPENSE_ID, result.getId());
        verify(expenseRepository).findById(EXPENSE_ID);
    }

    @Test
    void getExpenseByUserEmail_ShouldReturnPageOfExpenses() {
        Pageable pageable = mock(Pageable.class);
        Page<Expense> expensePage = new PageImpl<>(Collections.singletonList(testExpense));

        when(expenseRepository.findByUserEmail(TEST_EMAIL, pageable)).thenReturn(expensePage);
        when(expenseMapper.entityToDTO(testExpense)).thenReturn(testExpenseDTO);

        Page<ExpenseDTO> result = expenseService.getExpenseByUserEmail(TEST_EMAIL, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(expenseRepository).findByUserEmail(TEST_EMAIL, pageable);
    }

    @Test
    void getAllExpenseByStatus_ShouldReturnPageOfExpenses() {
        Pageable pageable = mock(Pageable.class);
        Page<Expense> expensePage = new PageImpl<>(Collections.singletonList(testExpense));

        when(expenseRepository.findByStatus(ExpenseStatus.PENDING, pageable)).thenReturn(expensePage);
        when(expenseMapper.entityToDTO(testExpense)).thenReturn(testExpenseDTO);

        Page<ExpenseDTO> result = expenseService.getAllExpenseByStatus(ExpenseStatus.PENDING, pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(expenseRepository).findByStatus(ExpenseStatus.PENDING, pageable);
    }

    @Test
    void getExpenseSummaryByDateRange_ShouldReturnSummary() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();
        ExpenseSummary expectedSummary = new ExpenseSummary();

        when(expenseSummaryService.generateExpenseSummary(startDate, endDate))
                .thenReturn(CompletableFuture.completedFuture(expectedSummary));

        ExpenseSummary result = expenseService.getExpenseSummaryByDateRange(startDate, endDate);

        assertNotNull(result);
        verify(expenseSummaryService).generateExpenseSummary(startDate, endDate);
    }

    @Test
    void validate_WhenAmountExceedsLimit_ShouldReturnError() {
        testInsertDTO.setAmount(200000.0);

        Result<Void> result = expenseService.validate(testInsertDTO);

        assertFalse(result.isSuccess());
        assertNotNull(result);
    }

    @Test
    void createExpense_ShouldCreateAndReturnExpense() {
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(expenseMapper.insertDtoToEntity(testInsertDTO)).thenReturn(testExpense);
        when(expenseMapper.entityToDTO(testExpense)).thenReturn(testExpenseDTO);
        when(expenseRepository.saveAndFlush(any(Expense.class))).thenReturn(testExpense);

        ExpenseDTO result = expenseService.createExpense(testInsertDTO, TEST_EMAIL, ExpenseStatus.PENDING);

        assertNotNull(result);
        verify(expenseRepository).saveAndFlush(any(Expense.class));
    }

    @Test
    void approveExpense_WhenExpenseIsPending_ShouldApproveAndReturn() {
        when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(testExpense));
        when(userRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testUser));
        when(expenseMapper.entityToDTO(any(Expense.class))).thenReturn(testExpenseDTO);

        ExpenseDTO result = expenseService.approveExpense(EXPENSE_ID, TEST_EMAIL);

        assertNotNull(result);
        verify(expenseRepository).saveAndFlush(any(Expense.class));
    }

    @Test
    void approveExpense_WhenExpenseNotPending_ShouldThrowException() {
        testExpense.setStatus(ExpenseStatus.APPROVED);
        when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(testExpense));

        assertThrows(IllegalArgumentException.class,
                () -> expenseService.approveExpense(EXPENSE_ID, TEST_EMAIL));
    }

    @Test
    void rejectExpense_WhenExpenseIsPending_ShouldRejectAndReturn() {
        ExpenseRejectDTO rejectDTO = new ExpenseRejectDTO();
        rejectDTO.setExpenseId(EXPENSE_ID);
        rejectDTO.setRejectReason("Invalid expense");

        when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(testExpense));
        when(expenseMapper.entityToDTO(any(Expense.class))).thenReturn(testExpenseDTO);

        ExpenseDTO result = expenseService.rejectExpense(rejectDTO);

        assertNotNull(result);
        verify(expenseRepository).saveAndFlush(any(Expense.class));
    }

    @Test
    void softDeleteExpenseById_ShouldMarkExpenseAsDeleted() {
        when(expenseRepository.findById(EXPENSE_ID)).thenReturn(Optional.of(testExpense));

        expenseService.softDeleteExpenseById(EXPENSE_ID);

        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    void validateApproveOrReject_WhenExpenseNotPending_ShouldThrowException() {
        testExpense.setStatus(ExpenseStatus.APPROVED);

        assertThrows(IllegalArgumentException.class,
                () -> expenseService.validateApproveOrReject(testExpense));
    }
}