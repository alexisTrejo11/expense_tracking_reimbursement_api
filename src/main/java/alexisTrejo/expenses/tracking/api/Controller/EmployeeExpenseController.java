package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
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
@RequestMapping("/v1/api/employee-expenses")
public class EmployeeExpenseController {

    private final ExpenseService expenseService;
    private final JWTSecurity jwtSecurity;

    @Autowired
    public EmployeeExpenseController(ExpenseService expenseService, JWTSecurity jwtSecurity) {
        this.expenseService = expenseService;
        this.jwtSecurity = jwtSecurity;
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getMyExpenses(HttpServletRequest request,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<ExpenseDTO> expenseDTOPage = expenseService.getExpenseByUserId(userIdResult.getData(), pageable);
        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTOPage, "Expense Data Successfully Fetched"));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> RequestExpence(@Valid @RequestBody ExpenseInsertDTO expenseInsertDTO,
                                                                            BindingResult bindingResult,
                                                                            HttpServletRequest request) {
        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        expenseService.createExpense(expenseInsertDTO, userIdResult.getData(), ExpenseStatus.PENDING);

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Expense Successfully Requested"));
    }
}
