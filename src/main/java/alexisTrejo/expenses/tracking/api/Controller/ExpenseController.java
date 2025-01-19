package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseRejectDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import alexisTrejo.expenses.tracking.api.Utils.Validations;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/api/manager/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;
    private final JWTSecurity jwtSecurity;
    private final NotificationService notificationService;

    @Autowired
    public ExpenseController(ExpenseService expenseService,
                             JWTSecurity jwtSecurity,
                             NotificationService notificationService) {
        this.expenseService = expenseService;
        this.jwtSecurity = jwtSecurity;
        this.notificationService = notificationService;
    }

    @Operation(summary = "Get Expense by ID", description = "Fetch an expense by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense data successfully fetched."),
            @ApiResponse(responseCode = "404", description = "Expense not found.")
    })
    @GetMapping("/{expenseId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ResponseWrapper<ExpenseDTO>> getExpenseById(@PathVariable Long expenseId) {
        Result<ExpenseDTO> expenseResult = expenseService.getExpenseById(expenseId);
        if (!expenseResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(expenseResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(expenseResult.getData(), "Expense Data Successfully Fetched"));
    }

    @Operation(summary = "Get Expenses by User ID", description = "Fetch expenses associated with a user by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense data successfully fetched."),
    })
    @GetMapping("/by-user/{userId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getExpenseByUserId(@PathVariable Long userId,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExpenseDTO> expenseDTOPage = expenseService.getExpenseByUserId(userId, pageable);

        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTOPage, "Expense Data Successfully Fetched"));
    }

    @Operation(summary = "Get Expenses by Status", description = "Fetch expenses based on their status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense data successfully fetched."),
    })
    @GetMapping("/by-status")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getExpensesByStatus(@RequestParam String status,
                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size,
                                                                                 @RequestParam(defaultValue = "true") Boolean isSortedASC) {
        Sort.Direction direction = !isSortedASC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "createdAt");

        // If params status received is not valid return PENDING as default
        ExpenseStatus expenseStatus = ExpenseStatus.findStatus(status).orElse(ExpenseStatus.PENDING);

        Pageable sortedPage = PageRequest.of(page, size, sort);
        Page<ExpenseDTO> expenseDTOPage = expenseService.getAllExpenseByStatus(expenseStatus, sortedPage);

        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTOPage, "Expense Data Successfully Fetched. Sorted By: " + expenseStatus.toString() + " (" + direction +")"));
    }

    @Operation(summary = "Get Expense Summary by Date Range", description = "Fetch the summary of expenses within a specified date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense summary successfully fetched."),
    })
    @GetMapping("/summary")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseWrapper<ExpenseSummary> getExpenseSummaryByDateRange(@RequestParam(required = false) LocalDateTime startDate,
                                                                        @RequestParam(required = false) LocalDateTime endDate) {

        // If both startDate or endDate are null, get the current month summary
        if (startDate == null || endDate == null) {
            LocalDate currentDate = LocalDate.now();
            LocalDateTime startMonth = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(), 1, 0, 0);
            LocalDateTime endMonth = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(), currentDate.lengthOfMonth(), 23, 59, 59);

            // Fetch the monthly summary
            ExpenseSummary monthlySummary = expenseService.getExpenseSummaryByDateRange(startMonth, endMonth);
            return ResponseWrapper.ok(monthlySummary, "Monthly Expense Summary Successfully Fetched With Date Range: " + monthlySummary.getSummaryDateRange());
        }

        ExpenseSummary expenseSummary = expenseService.getExpenseSummaryByDateRange(startDate, endDate);

        return ResponseWrapper.ok(expenseSummary, "Expense Summary Successfully Fetched With Date Range: " + expenseSummary.getSummaryDateRange());
    }

    @Operation(summary = "Approve Expense", description = "Approve a specific expense by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully approved."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "404", description = "Expense not found.")
    })
    @PutMapping("{expenseId}/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> approveExpense(HttpServletRequest request,
                                                                            @PathVariable Long expenseId) {
        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Result<ExpenseDTO> expenseResult = expenseService.approveExpense(expenseId, userIdResult.getData());
        if (!expenseResult.isSuccess()) {
            return ResponseEntity.status(expenseResult.getStatus()).body(ResponseWrapper.error(expenseResult.getErrorMessage(), expenseResult.getStatus().value()));
        }

        // Run in another thread, create and send the notification
        notificationService.sendNotificationFromExpense(expenseResult.getData());

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Expense With Id " + expenseId + " Successfully Approved"));
    }

    @Operation(summary = "Reject Expense", description = "Reject a specific expense with a reason.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully rejected."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "400", description = "Bad request due to validation errors."),
            @ApiResponse(responseCode = "404", description = "Expense not found.")
    })
    @PutMapping("/{expenseId}/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> rejectExpenseStatus(HttpServletRequest request,
                                                                                 @Valid ExpenseRejectDTO expenseRejectDTO,
                                                                                 BindingResult bindingResult) {
        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        Result<ExpenseDTO> expenseResult = expenseService.rejectExpense(expenseRejectDTO);
        if (!expenseResult.isSuccess()) {
            return ResponseEntity.status(expenseResult.getStatus()).body(ResponseWrapper.error(expenseResult.getErrorMessage(), expenseResult.getStatus().value()));
        }

        // Run in another thread and create and send the notification
        notificationService.sendNotificationFromExpense(expenseResult.getData());

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Expense With Id " + expenseRejectDTO.getExpenseId() + " Successfully Rejected"));
    }

    @Operation(summary = "Soft Delete Expense by ID", description = "Soft delete an expense by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully deleted."),
            @ApiResponse(responseCode = "404", description = "Expense not found.")
    })
    @DeleteMapping("/{expenseId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ResponseWrapper<Void>> deleteExpense(@PathVariable Long expenseId) {
        Result<Void> result = expenseService.softDeleteExpenseById(expenseId);
        if (!result.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(result.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Expense Successfully Deleted"));
    }
}
