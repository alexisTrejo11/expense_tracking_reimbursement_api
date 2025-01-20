package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseRejectDTO;
import alexisTrejo.expenses.tracking.api.Auth.JWTService;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/api/manager/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;
    private final JWTService jwtService;
    private final NotificationService notificationService;

    @Operation(summary = "Get Expense by ID", description = "Fetch an expense by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense data successfully fetched."),
            @ApiResponse(responseCode = "404", description = "Expense not found.")
    })
    @GetMapping("/{expenseId}")
    public ResponseEntity<ResponseWrapper<ExpenseDTO>> getExpenseById(@PathVariable Long expenseId) {
        ExpenseDTO expenseDTO = expenseService.getExpenseById(expenseId);
        if (expenseDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.notFound("Expense", "ID", expenseId));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTO, "Expense Data Successfully Fetched"));
    }

    @Operation(summary = "Get Expenses by User ID", description = "Fetch expenses associated with a user by user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense data successfully fetched."),
    })
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getExpenseByUserId(@PathVariable String email,
                                                                                @RequestParam(defaultValue = "0") int page,
                                                                                @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExpenseDTO> expenseDTOPage = expenseService.getExpenseByUserEmail(email, pageable);

        return ResponseEntity.ok(ResponseWrapper.ok(expenseDTOPage, "Expense Data Successfully Fetched"));
    }

    @Operation(summary = "Get Expenses by Status", description = "Fetch expenses based on their status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense data successfully fetched."),
    })
    @GetMapping("/by-status")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> getExpensesByStatus(@RequestParam String status,
                                                                                 @RequestParam(defaultValue = "0") int page,
                                                                                 @RequestParam(defaultValue = "10") int size,
                                                                                 @RequestParam(defaultValue = "true") Boolean isSortedASC) {
        Sort.Direction direction = !isSortedASC ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, "createdAt");

        // Pending Status as default to avoid null problems
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
    public ResponseWrapper<ExpenseSummary> getExpenseSummaryByDateRange(@RequestParam(required = false) LocalDateTime startDate,
                                                                        @RequestParam(required = false) LocalDateTime endDate) {

        // Default range (current month)
        if (startDate == null || endDate == null) {
            LocalDate currentDate = LocalDate.now();
            LocalDateTime startMonth = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(), 1, 0, 0);
            LocalDateTime endMonth = LocalDateTime.of(currentDate.getYear(), currentDate.getMonth(), currentDate.lengthOfMonth(), 23, 59, 59);

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
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> approveExpense(HttpServletRequest request,
                                                                            @PathVariable Long expenseId) {
        String email= jwtService.getEmailFromRequest(request);

        ExpenseDTO expense = expenseService.approveExpense(expenseId, email);

        notificationService.sendNotificationFromExpense(expense);

        return ResponseEntity.ok(ResponseWrapper.ok("Expense With Id " + expenseId + " Successfully Approved"));
    }

    @Operation(summary = "Reject Expense", description = "Reject a specific expense with a reason.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully rejected."),
            @ApiResponse(responseCode = "401", description = "Unauthorized access."),
            @ApiResponse(responseCode = "400", description = "Bad request due to validation errors."),
            @ApiResponse(responseCode = "404", description = "Expense not found.")
    })
    @PutMapping("/{expenseId}/reject")
    public ResponseEntity<ResponseWrapper<Page<ExpenseDTO>>> rejectExpenseStatus(@Valid ExpenseRejectDTO expenseRejectDTO) {
        ExpenseDTO expense = expenseService.rejectExpense(expenseRejectDTO);

        notificationService.sendNotificationFromExpense(expense);

        return ResponseEntity.ok(ResponseWrapper.ok("Expense With Id " + expenseRejectDTO.getExpenseId() + " Successfully Rejected"));
    }

    @Operation(summary = "Soft Delete Expense by ID", description = "Soft delete an expense by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Expense successfully deleted."),
            @ApiResponse(responseCode = "404", description = "Expense not found."),
            @ApiResponse(responseCode = "500", description = "Server Error.")
    })
    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteExpense(@PathVariable Long expenseId) {
        expenseService.softDeleteExpenseById(expenseId);
        return ResponseEntity.ok(ResponseWrapper.deleted("Expense"));
    }
}
