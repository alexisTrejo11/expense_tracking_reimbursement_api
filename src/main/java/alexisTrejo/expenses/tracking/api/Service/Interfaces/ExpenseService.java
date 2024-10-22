package alexisTrejo.expenses.tracking.api.Service.Interfaces;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseRejectDTO;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ExpenseService {
    Result<ExpenseDTO> getExpenseById(Long expenseId);
    Page<ExpenseDTO> getExpenseByUserId(Long userId, Pageable pageable);
    Page<ExpenseDTO> getAllExpenseByStatus(ExpenseStatus expenseStatus, Pageable sortedPage);

    ExpenseSummary getExpenseSummaryByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    ExpenseDTO createExpense(ExpenseInsertDTO expenseInsertDTO, Long userId, ExpenseStatus expenseStatus);
    Result<ExpenseDTO> approveExpense(Long expenseId, Long managerId);
    Result<ExpenseDTO> rejectExpense(ExpenseRejectDTO expenseRejectDTO);

    Result<Void> softDeleteExpenseById(Long expenseId);

}
