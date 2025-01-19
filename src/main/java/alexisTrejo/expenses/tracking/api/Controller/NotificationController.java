package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Get Notification by ID", description = "Retrieve a notification by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification successfully fetched."),
            @ApiResponse(responseCode = "404", description = "Notification not found.")
    })
    @GetMapping("/{notificationId}")
    public ResponseEntity<ResponseWrapper<NotificationDTO>> getNotificationById(@PathVariable Long notificationId) {
        Result<NotificationDTO> notificationResult = notificationService.getNotificationById(notificationId);
        if (!notificationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(notificationResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(notificationResult.getData(), "Notification With Id(" + notificationId + ") Successfully Fetched"));
    }

    @Operation(summary = "Get Notifications by User ID", description = "Retrieve all notifications for a specific user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notifications successfully fetched."),
            @ApiResponse(responseCode = "404", description = "No notifications found for the user.")
    })
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

    @Operation(summary = "Create Notification", description = "Create a new notification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification successfully created."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> createNotification(@Valid @RequestBody NotificationInsertDTO notificationInsertDTO) {
        notificationService.createNotification(notificationInsertDTO);

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Notification Successfully Created"));
    }

    @Operation(summary = "Mark Notification as Read", description = "Mark a notification as read by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification successfully marked as read."),
            @ApiResponse(responseCode = "404", description = "Notification not found.")
    })
    @PutMapping("set-as-read/{notificationId}")
    public ResponseEntity<ResponseWrapper<NotificationDTO>> markNotificationAsRead(@PathVariable Long notificationId) {
        Result<Void> notificationResult = notificationService.markNotificationAsRead(notificationId);
        if (!notificationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound(notificationResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Notification With Id(" + notificationId + ") Successfully Marked As Read"));
    }
}
