package alexisTrejo.expenses.tracking.api.Service.Interfaces;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseRejectDTO;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ExpenseService {
    ExpenseDTO getExpenseById(Long expenseId);
    Page<ExpenseDTO> getExpenseByUserEmail(String email, Pageable pageable);
    Page<ExpenseDTO> getAllExpenseByStatus(ExpenseStatus expenseStatus, Pageable sortedPage);

    ExpenseSummary getExpenseSummaryByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    ExpenseDTO createExpense(ExpenseInsertDTO expenseInsertDTO, String email, ExpenseStatus expenseStatus);
    ExpenseDTO approveExpense(Long expenseId, String managerEmail);
    ExpenseDTO rejectExpense(ExpenseRejectDTO expenseRejectDTO);

    void softDeleteExpenseById(Long expenseId);
}
