package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationInsertDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.NotificationService;
import alexisTrejo.expenses.tracking.api.Utils.MessageGenerator;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
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
    private final MessageGenerator message;

    @Operation(summary = "Get Notification by ID", description = "Retrieve a notification by its unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification successfully fetched."),
            @ApiResponse(responseCode = "404", description = "Notification not found.")
    })
    @GetMapping("/{notificationId}")
    public ResponseEntity<ResponseWrapper<NotificationDTO>> getNotificationById(@PathVariable Long id) {
        NotificationDTO notification = notificationService.getNotificationById(id);
        if (notification == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Notification","ID", id));
        }

        return ResponseEntity.ok(ResponseWrapper.found(notification, "Notification", "ID", id));
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
        Page<NotificationDTO> notifications = notificationService.getNotificationByUserId(userId, pageable);

        return ResponseEntity.ok(ResponseWrapper.found(notifications, "Notifications", "userID", userId));
    }

    @Operation(summary = "Create Notification", description = "Create a new notification.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification successfully created."),
            @ApiResponse(responseCode = "400", description = "Invalid input data.")
    })
    @PostMapping
    public ResponseEntity<ResponseWrapper<Void>> createNotification(@Valid @RequestBody NotificationInsertDTO notificationInsertDTO) {
        notificationService.createNotification(notificationInsertDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created("Notification"));
    }

    @Operation(summary = "Mark Notification as Read", description = "Mark a notification as read by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification successfully marked as read."),
            @ApiResponse(responseCode = "404", description = "Notification not found.")
    })
    @PutMapping("set-as-read/{notificationId}")
    public ResponseEntity<ResponseWrapper<NotificationDTO>> markNotificationAsRead(@PathVariable Long notificationId) {
       notificationService.markNotificationAsRead(notificationId);

        return ResponseEntity.ok(ResponseWrapper.success(message.successAction("Notification", "mark as read")));
    }
}
