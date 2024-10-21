package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReimbursementDomainService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Result<Void> setReimbursementRelationShips(ReimbursementInsertDTO reimbursementInsertDTO, Reimbursement reimbursement, Long userId) {
        Long expenseId = reimbursementInsertDTO.getExpenseId();
        reimbursement.setProcessedBy(new User(userId));

        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        if (optionalExpense.isEmpty()) {
            return Result.error("Expense With Id(" + expenseId + ") Not Found", HttpStatus.NOT_FOUND);
        }

        // Validate Expense
        ExpenseStatus expenseStatus = optionalExpense.get().getStatus();
        return switch (expenseStatus) {
            case REIMBURSED -> Result.error("Expense With Id(" + expenseId + ") Already Has Been Reimbursed", HttpStatus.BAD_REQUEST);
            case PENDING -> Result.error("Expense With Id(" + expenseId + ") Is Not Approved", HttpStatus.BAD_REQUEST);
            case REJECTED -> Result.error("Expense With Id(" + expenseId + ") Has Been Rejected", HttpStatus.BAD_REQUEST);
            case APPROVED -> {
                reimbursement.setExpense(optionalExpense.get());
                yield Result.success();
            }
            default -> Result.error("Invalid Expense Status", HttpStatus.BAD_REQUEST);
        };
    }
}
