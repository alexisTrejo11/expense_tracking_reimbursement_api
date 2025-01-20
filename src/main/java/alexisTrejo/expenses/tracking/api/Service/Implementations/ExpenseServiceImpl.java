package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseRejectDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ExpenseMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final UserRepository userRepository;
    private final ExpenseSummaryService expenseSummaryService;

    @Override
    public ExpenseDTO getExpenseById(Long expenseId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        return optionalExpense
                .map(expenseMapper::entityToDTO)
                .orElse(null);
    }

    @Override
    public Page<ExpenseDTO> getExpenseByUserEmail(String email, Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findByUserEmail(email, pageable);
        return expenses.map(expenseMapper::entityToDTO);
    }


    @Override
    @Cacheable(value = "expensesByStatusCache", key = "#expenseStatus")
    public Page<ExpenseDTO> getAllExpenseByStatus(ExpenseStatus expenseStatus, Pageable sortedPageable) {
        Page<Expense> expenses = expenseRepository.findByStatus(expenseStatus, sortedPageable);

        return expenses.map(expenseMapper::entityToDTO);
    }

    @Override
    @Cacheable(value = "expenseSummaryCache", key = "'summary_' + #startDate + '_' + #endDate")
    public ExpenseSummary getExpenseSummaryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
            CompletableFuture<ExpenseSummary> expenseSummaryFuture = expenseSummaryService.generateExpenseSummary(startDate, endDate);
            return expenseSummaryFuture.join();
    }


    @Override
    @Transactional
    public ExpenseDTO createExpense(ExpenseInsertDTO expenseInsertDTO, String email, ExpenseStatus expenseStatus) {

        Expense expense = expenseMapper.insertDtoToEntity(expenseInsertDTO);
        expense.setStatus(expenseStatus);
        expense.setUser(getUser(email));

        expenseRepository.saveAndFlush(expense);
        return expenseMapper.entityToDTO(expense);

    }

    @Override
    @Transactional
    public Result<ExpenseDTO> approveExpense(Long expenseId, String managerEmail) {
       Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
       if (optionalExpense.isEmpty()) {
           return Result.error("Expense with ID " + expenseId + " not found", HttpStatus.NOT_FOUND);

       }
       Expense expense = optionalExpense.get();

        if (!isExpensePending(expense)) {
            return Result.error("Expense with ID " + expenseId + " has already been processed", HttpStatus.BAD_REQUEST);
        }

        expense.setApprovedBy(getUser(managerEmail));
        expense.setStatus(ExpenseStatus.APPROVED);
        expenseRepository.save(expense);

        return Result.success(expenseMapper.entityToDTO(expense));
    }

    @Override
    @Transactional
    public Result<ExpenseDTO> rejectExpense(ExpenseRejectDTO expenseRejectDTO) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseRejectDTO.getExpenseId());
        if (optionalExpense.isEmpty()) {
            return Result.error("Expense with ID " + expenseRejectDTO.getExpenseId() + " not found", HttpStatus.NOT_FOUND);

        }
        Expense expense = optionalExpense.get();

        if (!isExpensePending(expense)) {
            return Result.error("Expense with ID " + expenseRejectDTO.getExpenseId() + " has already been processed", HttpStatus.BAD_REQUEST);
        }

        expense.setAsRejected(expenseRejectDTO.getRejectReason());
        expenseRepository.save(expense);

        return Result.success(expenseMapper.entityToDTO(expense));
    }

    @Override
    @Transactional
    public Result<Void> softDeleteExpenseById(Long expenseId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        return optionalExpense
                .map(expense -> {
                    expense.setAsDeleted();
                    expenseRepository.saveAndFlush(expense);
                    return Result.success();
                })
                .orElseGet(() -> Result.error("Expense With Id(" + expenseId + ") not found"));
    }


    private boolean isExpensePending(Expense expense) {
        return expense.getStatus() == ExpenseStatus.PENDING;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User with email [" + email + "] not found"));
    }
}

