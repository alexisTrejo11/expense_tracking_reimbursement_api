package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.User.ProfileDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Service.UserService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    private final JWTSecurity jwtSecurity;
    private final UserService userService;

    public UserController(JWTSecurity jwtSecurity, UserService userService) {
        this.jwtSecurity = jwtSecurity;
        this.userService = userService;
    }

    @GetMapping("/my-profile")
    public ResponseEntity<ResponseWrapper<ProfileDTO>> getMyProfile(HttpServletRequest request) {
        Long userId = jwtSecurity.getUserIdFromToken(request);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.error("Unauthorized", HttpStatus.UNAUTHORIZED.value()));
        }

        Result<ProfileDTO> userResult = userService.getProfileById(userId);
        if (!userResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(userResult.getErrorMessage(), HttpStatus.NOT_FOUND.value()));
        }

        return  ResponseEntity.ok(ResponseWrapper.ok(userResult.getData(), "User Profile Successfully Fetched"));
    }
}
