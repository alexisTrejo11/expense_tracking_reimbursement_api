package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ReimbursementService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/api/reimbursements")
@RequiredArgsConstructor
public class ReimbursementController {

    private final ReimbursementService reimbursementService;
    private final NotificationService notificationService;
    private final JWTSecurity jwtSecurity;


    @Operation(summary = "Get Reimbursements by User ID", description = "Retrieve all reimbursements for a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reimbursements successfully fetched."),
            @ApiResponse(responseCode = "404", description = "No reimbursements found for the user.")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'FINANCIAL')")
    public ResponseEntity<ResponseWrapper<Page<ReimbursementDTO>>> getReimbursementByUserId(@PathVariable Long userId,
                                                                                            @RequestParam(defaultValue = "0") int page,
                                                                                            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        Result<Page<ReimbursementDTO>> reimbursementResult = reimbursementService.getReimbursementByUserId(userId, pageable);
        if (!reimbursementResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(reimbursementResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(reimbursementResult.getData(), "Reimbursements successfully fetched by user Id("+ userId + ")"));
    }

    @Operation(summary = "Get Reimbursement by ID", description = "Retrieve a reimbursement by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reimbursement successfully fetched."),
            @ApiResponse(responseCode = "404", description = "Reimbursement not found.")
    })
    @GetMapping("/{reimbursementId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'FINANCIAL')")
    public ResponseEntity<ResponseWrapper<ReimbursementDTO>> getReimbursementById(@PathVariable Long reimbursementId) {
        Result<ReimbursementDTO> reimbursementResult = reimbursementService.getReimbursementById(reimbursementId);
        if (!reimbursementResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(reimbursementResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(reimbursementResult.getData(), "Reimbursements successfully fetched by Id("+ reimbursementId + ")"));
    }

    @Operation(summary = "Create Reimbursement", description = "Create a new reimbursement.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reimbursement successfully created."),
            @ApiResponse(responseCode = "400", description = "Invalid input data."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user.")
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'FINANCIAL')")
    public ResponseEntity<ResponseWrapper<Void>> createReimbursement(@Valid @RequestBody ReimbursementInsertDTO reimbursementInsertDTO,
                                                                     HttpServletRequest request) {
        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Result<ReimbursementDTO> createResult = reimbursementService.createReimbursement(reimbursementInsertDTO, userIdResult.getData());
        if (!createResult.isSuccess()){
            return ResponseEntity.status(createResult.getStatus()).body(ResponseWrapper.badRequest(createResult.getErrorMessage()));
        }

        notificationService.sendNotificationFromExpense(createResult.getData().getExpense());

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Reimbursement successfully created"));
    }
}
