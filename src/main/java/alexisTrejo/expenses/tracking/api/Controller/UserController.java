package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.UserService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    private final JWTSecurity jwtSecurity;
    private final UserService userService;

    public UserController(JWTSecurity jwtSecurity, UserService userService) {
        this.jwtSecurity = jwtSecurity;
        this.userService = userService;
    }

    @Operation(summary = "Get My Profile", description = "Retrieve the profile information of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile successfully fetched."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user."),
            @ApiResponse(responseCode = "404", description = "User profile not found.")
    })
    @GetMapping("/my-profile")
    @PreAuthorize("hasAnyRole('EMPLOYEE','MANAGER', 'FINANCIAL', 'ADMIN')")
    public ResponseEntity<ResponseWrapper<ProfileDTO>> getMyProfile(HttpServletRequest request) {
        Result<Long> userIdResult = jwtSecurity.getUserIdFromToken(request);
        if (!userIdResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(userIdResult.getErrorMessage()));
        }

        Result<ProfileDTO> userResult = userService.getProfileById(userIdResult.getData());
        if (!userResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(userResult.getErrorMessage(), HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(userResult.getData(), "User Profile Successfully Fetched"));
    }
}
