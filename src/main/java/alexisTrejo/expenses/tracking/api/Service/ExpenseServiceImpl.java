package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseRejectDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ExpenseMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;
    private final ExpenseDomainService expenseDomainService;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseMapper expenseMapper,
                              ExpenseDomainService expenseDomainService) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
        this.expenseDomainService = expenseDomainService;
    }

    @Override
    public Result<ExpenseDTO> getExpenseById(Long expenseId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        return optionalExpense
                .map(expense -> Result.success(expenseMapper.entityToDTO(expense)))
                .orElseGet(() -> Result.error("Expense With Id(" + expenseId + ") not found"));
    }

    @Override
    public Page<ExpenseDTO> getExpenseByUserId(Long userId, Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findByUserId(userId, pageable);
        return expenses.map(expenseMapper::entityToDTO);
    }


    public Page<ExpenseDTO> getAllExpenseByStatus(ExpenseStatus expenseStatus, Pageable sortedPageable) {
        Page<Expense> expenses = expenseRepository.findByStatus(expenseStatus, sortedPageable);

        return expenses.map(expenseMapper::entityToDTO);
    }

    @Override
    public ExpenseSummary getExpenseSummaryByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
            CompletableFuture<ExpenseSummary> expenseSummaryFuture = expenseDomainService.generateExpenseSummary(startDate, endDate);
            return expenseSummaryFuture.join();
    }


    @Override
    @Transactional
    public void createExpense(ExpenseInsertDTO expenseInsertDTO, Long userId, ExpenseStatus expenseStatus) {
        Expense expense = expenseMapper.insertDtoToEntity(expenseInsertDTO);
        expense.setStatus(expenseStatus);
        expense.setUserId(userId);

        expenseRepository.saveAndFlush(expense);
    }

    @Override
    @Transactional
    public Result<Void> approveExpense(Long expenseId, Long managerId) {
       Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
       if (optionalExpense.isEmpty()) {
           return Result.error("Expense with ID " + expenseId + " not found", HttpStatus.NOT_FOUND);

       }
       Expense expense = optionalExpense.get();

        if (!isExpensePending(expense)) {
            return Result.error("Expense with ID " + expenseId + " has already been processed", HttpStatus.BAD_REQUEST);
        }

        expense.setApprovedBy(new User(managerId));
        expense.setStatus(ExpenseStatus.APPROVED);
        expenseRepository.save(expense);

        return Result.success();
    }

    @Override
    @Transactional
    public Result<Void> rejectExpense(ExpenseRejectDTO expenseRejectDTO) {
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

        return Result.success();
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
}

