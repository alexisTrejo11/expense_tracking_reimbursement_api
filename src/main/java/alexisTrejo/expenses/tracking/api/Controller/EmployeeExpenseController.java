package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/api/employees/expenses")
public class EmployeeExpenseController {

    private final ExpenseService expenseService;
    private final JWTSecurity jwtSecurity;
    private final NotificationService notificationService;

    @Autowired
    public EmployeeExpenseController(ExpenseService expenseService,
                                     JWTSecurity jwtSecurity,
                                     NotificationService notificationService) {
        this.expenseService = expenseService;
        this.jwtSecurity = jwtSecurity;
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get expenses by user ID",
            description = "Fetches a paginated list of expenses for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched expenses"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('Employee')")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getMyExpenses(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<ExpenseDTO> expenseDTOPage = expenseService.getExpenseByUserId(userIdResult.getData(), pageable);
        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTOPage, "Expense Data Successfully Fetched"));
    }

    @Operation(summary = "Request a new expense",
            description = "Creates a new expense based on the provided data.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully requested"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<Void>> RequestExpense(
            @Valid @RequestBody ExpenseInsertDTO expenseInsertDTO,
            BindingResult bindingResult,
            HttpServletRequest request) {

        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        ExpenseDTO expenseDTO = expenseService.createExpense(expenseInsertDTO, userIdResult.getData(), ExpenseStatus.PENDING);
        notificationService.sendNotificationFromExpense(expenseDTO);

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Expense Successfully Requested"));
    }
}
