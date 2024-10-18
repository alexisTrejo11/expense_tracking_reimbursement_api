package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Service.ExpenseService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Validations;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final JWTSecurity jwtSecurity;

    @Autowired
    public ExpenseController(ExpenseService expenseService,
                             JWTSecurity jwtSecurity) {
        this.expenseService = expenseService;
        this.jwtSecurity = jwtSecurity;
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

    @PostMapping
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> createExpense(@Valid @RequestBody ExpenseInsertDTO expenseInsertDTO,
                                                                           BindingResult bindingResult,
                                                                           HttpServletRequest request) {
        Long userId = jwtSecurity.getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.error("Unauthorized", HttpStatus.UNAUTHORIZED.value()));
        }


        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        expenseService.CreateExpense(expenseInsertDTO, userId);

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Expense Successfully Created"));
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
