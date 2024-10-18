package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseService {
    Result<ExpenseDTO> GetExpenseById(Long expenseId);
    Page<ExpenseDTO> GetExpenseByUserId(Long userId, Pageable pageable);
    void CreateExpense(ExpenseInsertDTO expenseInsertDTO, Long userId);
    Result<Void> SoftDeleteExpenseById(Long expenseId);

}
