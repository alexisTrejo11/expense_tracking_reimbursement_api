package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Service.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Validations;
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
@RequestMapping("/v1/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{notificationId}")
    public ResponseEntity<ResponseWrapper<NotificationDTO>> getNotificationById(@PathVariable Long notificationId) {
        Result<NotificationDTO> notificationResult = notificationService.getNotificationById(notificationId);
        if (!notificationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(notificationResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(notificationResult.getData(), "Notification With Id(" + notificationId + ") Successfully Fetched"));
    }

    @GetMapping("by-user/{userId}")
    public ResponseEntity<ResponseWrapper<Page<NotificationDTO>>> getNotificationByUserId(@PathVariable Long userId,
                                                                                    @RequestParam(defaultValue = "0") int page,
                                                                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);

        Result<Page<NotificationDTO>> notificationResult = notificationService.getNotificationByUserId(userId, pageable);
        if (!notificationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(notificationResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(notificationResult.getData(), "Notifications With User Id(" + userId + ") Successfully Fetched"));
    }

    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> createNotification(@Valid @RequestBody NotificationInsertDTO notificationInsertDTO,
                                                                    BindingResult bindingResult) {
        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        notificationService.createNotification(notificationInsertDTO);

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Notification Successfully Created"));
    }


    @PutMapping("set-as-read/{notificationId}")
    public ResponseEntity<ResponseWrapper<NotificationDTO>> markNotificationAsRead(@PathVariable Long notificationId) {
        Result<Void> notificationResult = notificationService.markNotificationAsRead(notificationId);
        if (!notificationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(notificationResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Notification With Id(" + notificationId + ") Successfully Marked As Read"));
    }

}
