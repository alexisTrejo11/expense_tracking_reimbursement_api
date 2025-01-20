package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Auth.JWTService;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/api/employees/expenses")
@RequiredArgsConstructor
public class EmployeeExpenseController {

    private final ExpenseService expenseService;
    private final JWTService jwtService;
    private final NotificationService notificationService;

    @Operation(summary = "Get expenses by user ID",
            description = "Fetches a paginated list of expenses for the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully fetched expenses"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('Employee')")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getMyExpenses(HttpServletRequest request,
                                                                           @RequestParam(defaultValue = "0") int page,
                                                                           @RequestParam(defaultValue = "10") int size) {
        String email = jwtService.getEmailFromRequest(request);

        Pageable pageable = PageRequest.of(page, size);
        Page<ExpenseDTO> expenseDTOPage = expenseService.getExpenseByUserEmail(email, pageable);

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
    public ResponseEntity<ResponseWrapper<ExpenseDTO>> RequestExpense(@Valid @RequestBody ExpenseInsertDTO expenseInsertDTO,
                                                                      HttpServletRequest request) {
        String email = jwtService.getEmailFromRequest(request);

        ExpenseDTO expenseDTO = expenseService.createExpense(expenseInsertDTO, email, ExpenseStatus.PENDING);
        notificationService.sendNotificationFromExpense(expenseDTO);

        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTO, "Expense Successfully Requested"));
    }
}
