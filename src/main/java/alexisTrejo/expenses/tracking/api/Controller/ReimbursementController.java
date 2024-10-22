package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ReimbursementService;
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
@RequestMapping("/v1/api/reimbursements")
public class ReimbursementController {

    private final ReimbursementService reimbursementService;
    private final NotificationService notificationService;
    private final JWTSecurity jwtSecurity;

    @Autowired
    public ReimbursementController(ReimbursementService reimbursementService,
                                   NotificationService notificationService,
                                   JWTSecurity jwtSecurity) {
        this.reimbursementService = reimbursementService;
        this.notificationService = notificationService;
        this.jwtSecurity = jwtSecurity;
    }

    @GetMapping("/user/{userId}")
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

    @GetMapping("/{reimbursementId}")
    public ResponseEntity<ResponseWrapper<ReimbursementDTO>> getReimbursementById(@PathVariable Long reimbursementId) {
        Result<ReimbursementDTO> reimbursementResult = reimbursementService.getReimbursementById(reimbursementId);
        if (!reimbursementResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(reimbursementResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(reimbursementResult.getData(), "Reimbursements successfully fetched by Id("+ reimbursementId + ")"));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> createReimbursement(@Valid @RequestBody ReimbursementInsertDTO reimbursementInsertDTO,
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

        Result<ReimbursementDTO> createResult = reimbursementService.createReimbursement(reimbursementInsertDTO, userIdResult.getData());
        if (!createResult.isSuccess()){
            return ResponseEntity.status(createResult.getStatus()).body(ResponseWrapper.badRequest(createResult.getErrorMessage()));
        }

        // Create Notification in another thread async
        notificationService.sendNotificationFromExpense(createResult.getData().getExpense());

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Reimbursement successfully created"));
    }
}