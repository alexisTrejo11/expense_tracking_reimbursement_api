package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ReimbursementMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Repository.ReimbursementRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Implementations.ReimbursementServiceImpl;
import alexisTrejo.expenses.tracking.api.Utils.MessageGenerator;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReimbursementServiceImplTest {

    @Mock
    private ReimbursementRepository reimbursementRepository;

    @Mock
    private ReimbursementMapper reimbursementMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private MessageGenerator message;

    @InjectMocks
    private ReimbursementServiceImpl reimbursementService;

    private Reimbursement testReimbursement;
    private ReimbursementDTO testReimbursementDTO;
    private ReimbursementInsertDTO testInsertDTO;
    private User testUser;
    private Expense testExpense;
    private final Long REIMBURSEMENT_ID = 1L;
    private final Long USER_ID = 1L;
    private final Long EXPENSE_ID = 1L;
    private final String TEST_EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        testReimbursement = new Reimbursement();
        testReimbursement.setId(REIMBURSEMENT_ID);

        testReimbursementDTO = new ReimbursementDTO();
        testReimbursementDTO.setId(REIMBURSEMENT_ID);

        testInsertDTO = new ReimbursementInsertDTO();
        testInsertDTO.setExpenseId(EXPENSE_ID);

        testUser = new User();
        testUser.setId(USER_ID);
        testUser.setEmail(TEST_EMAIL);

        testExpense = new Expense();
        testExpense.setId(EXPENSE_ID);
        testExpense.setStatus(ExpenseStatus.APPROVED);
    }

    @Test
    void getReimbursementById_WhenExists_ShouldReturnDTO() {
        // Arrange
        when(reimbursementRepository.findById(REIMBURSEMENT_ID))
                .thenReturn(Optional.of(testReimbursement));
        when(reimbursementMapper.entityToDTO(testReimbursement))
                .thenReturn(testReimbursementDTO);

        // Act
        ReimbursementDTO result = reimbursementService.getReimbursementById(REIMBURSEMENT_ID);

        // Assert
        assertNotNull(result);
        assertEquals(REIMBURSEMENT_ID, result.getId());
        verify(reimbursementRepository).findById(REIMBURSEMENT_ID);
        verify(reimbursementMapper).entityToDTO(testReimbursement);
    }

    @Test
    void getReimbursementById_WhenNotExists_ShouldReturnNull() {
        // Arrange
        when(reimbursementRepository.findById(REIMBURSEMENT_ID))
                .thenReturn(Optional.empty());

        // Act
        ReimbursementDTO result = reimbursementService.getReimbursementById(REIMBURSEMENT_ID);

        // Assert
        assertNull(result);
        verify(reimbursementRepository).findById(REIMBURSEMENT_ID);
        verify(reimbursementMapper, never()).entityToDTO(any());
    }

    @Test
    void getReimbursementByUserId_WhenUserExists_ShouldReturnPage() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        Page<Reimbursement> reimbursementPage = new PageImpl<>(Arrays.asList(testReimbursement));

        when(userRepository.existsById(USER_ID)).thenReturn(true);
        when(reimbursementRepository.findByProcessedBy_Id(USER_ID, pageable))
                .thenReturn(reimbursementPage);
        when(reimbursementMapper.entityToDTO(testReimbursement))
                .thenReturn(testReimbursementDTO);

        // Act
        Page<ReimbursementDTO> result = reimbursementService.getReimbursementByUserId(USER_ID, pageable);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(userRepository).existsById(USER_ID);
        verify(reimbursementRepository).findByProcessedBy_Id(USER_ID, pageable);
    }

    @Test
    void getReimbursementByUserId_WhenUserNotExists_ShouldThrowException() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> reimbursementService.getReimbursementByUserId(USER_ID, pageable));
        verify(userRepository).existsById(USER_ID);
        verify(reimbursementRepository, never()).findByProcessedBy_Id(any(), any());
    }

    @Test
    void createReimbursement_ShouldCreateAndReturnDTO() {
        // Arrange
        when(reimbursementMapper.insertDtoToEntity(testInsertDTO))
                .thenReturn(testReimbursement);
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.of(testUser));
        when(expenseRepository.findById(EXPENSE_ID))
                .thenReturn(Optional.of(testExpense));
        when(reimbursementRepository.saveAndFlush(any(Reimbursement.class)))
                .thenReturn(testReimbursement);
        when(reimbursementMapper.entityToDTO(testReimbursement))
                .thenReturn(testReimbursementDTO);

        // Act
        ReimbursementDTO result = reimbursementService.createReimbursement(testInsertDTO, TEST_EMAIL);

        // Assert
        assertNotNull(result);
        verify(reimbursementMapper).insertDtoToEntity(testInsertDTO);
        verify(userRepository).findByEmail(TEST_EMAIL);
        verify(expenseRepository).findById(EXPENSE_ID);
        verify(reimbursementRepository).saveAndFlush(any(Reimbursement.class));
    }

    @Test
    void createReimbursement_WhenUserNotFound_ShouldThrowException() {
        // Arrange
        when(reimbursementMapper.insertDtoToEntity(testInsertDTO))
                .thenReturn(testReimbursement);
        when(userRepository.findByEmail(TEST_EMAIL))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> reimbursementService.createReimbursement(testInsertDTO, TEST_EMAIL));
        verify(reimbursementRepository, never()).saveAndFlush(any());
    }

    @Test
    void validate_WhenExpenseIsApproved_ShouldReturnSuccess() {
        // Arrange
        when(expenseRepository.findById(EXPENSE_ID))
                .thenReturn(Optional.of(testExpense));

        // Act
        Result<Void> result = reimbursementService.validate(testInsertDTO);

        // Assert
        assertTrue(result.isSuccess());
        verify(expenseRepository).findById(EXPENSE_ID);
    }

    @Test
    void validate_WhenExpenseNotApproved_ShouldReturnError() {
        // Arrange
        testExpense.setStatus(ExpenseStatus.PENDING);
        when(expenseRepository.findById(EXPENSE_ID))
                .thenReturn(Optional.of(testExpense));

        // Act
        Result<Void> result = reimbursementService.validate(testInsertDTO);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Only approved expenses can be reimbursement", result.getErrorMessage());
        verify(expenseRepository).findById(EXPENSE_ID);
    }

    @Test
    void validate_WhenExpenseNotFound_ShouldThrowException() {
        // Arrange
        when(expenseRepository.findById(EXPENSE_ID))
                .thenReturn(Optional.empty());
        when(message.notFoundPlain(anyString(), any()))
                .thenReturn("Expense not found");

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> reimbursementService.validate(testInsertDTO));
        verify(expenseRepository).findById(EXPENSE_ID);
    }
}