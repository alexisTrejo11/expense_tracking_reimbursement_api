package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Service.ExpenseService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/v1/api/manager/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Autowired
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping("/{expenseId}")
    public ResponseEntity<ResponseWrapper<ExpenseDTO>> getExpenseById(@PathVariable Long expenseId) {
        Result<ExpenseDTO> expenseResult = expenseService.GetExpenseById(expenseId);
        if (!expenseResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(expenseResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(expenseResult.getData(), "Expense Data Successfully Fetched"));
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getExpenseByUserId(@PathVariable Long userId,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExpenseDTO> expenseDTOPage = expenseService.GetExpenseByUserId(userId, pageable);

        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTOPage, "Expense Data Successfully Fetched"));
    }

    @GetMapping("/by-status")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getExpensesByStatus(@RequestParam String status,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size,
                                                                                @RequestParam(defaultValue = "true") Boolean isSortedASC) {
        Sort.Direction direction = !isSortedASC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "createdAt");

        // If params status received is not valid return PENDING as default
        ExpenseStatus expenseStatus = ExpenseStatus.findStatus(status).orElse(ExpenseStatus.PENDING);

        Pageable sortedPage = PageRequest.of(page, size, sort);
        Page<ExpenseDTO> expenseDTOPage = expenseService.GetAllExpenseByStatus(expenseStatus, sortedPage);

        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTOPage, "Expense Data Successfully Fetched. Sorted By: " + expenseStatus.toString() + " (" + direction +")"));
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ResponseWrapper<ExpenseDTO>> softDeleteExpenseById(@PathVariable Long expenseId) {
        Result<Void> deleteResult = expenseService.SoftDeleteExpenseById(expenseId);
        if (!deleteResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(deleteResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Expense Data Successfully Deleted"));
    }
}
