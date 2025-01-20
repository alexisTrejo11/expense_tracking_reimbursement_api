package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.Auth.JWTService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.UserService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/api/users")
@RequiredArgsConstructor
public class UserController {

    private final JWTService jwtService;
    private final UserService userService;

    @Operation(summary = "Get My Profile", description = "Retrieve the profile information of the authenticated user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile successfully fetched."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user."),
            @ApiResponse(responseCode = "404", description = "User profile not found.")
    })
    @GetMapping("/my-profile")
    public ResponseEntity<ResponseWrapper<ProfileDTO>> getMyProfile(HttpServletRequest request) {
        String email = jwtService.getEmailFromRequest(request);

        Result<ProfileDTO> userResult = userService.getProfileById(email);
        if (!userResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(userResult.getErrorMessage(), HttpStatus.NOT_FOUND.value()));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(userResult.getData(), "User Profile Successfully Fetched"));
    }
}
