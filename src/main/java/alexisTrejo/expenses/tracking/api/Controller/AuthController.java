package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.LoginDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.enums.Role;
import alexisTrejo.expenses.tracking.api.Service.AuthService;
import alexisTrejo.expenses.tracking.api.Service.UserService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Validations;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("v1/api/users")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Autowired
    public AuthController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<String>> registerEmployee(@Valid @RequestBody UserInsertDTO userInsertDTO,
                                                                    BindingResult bindingResult) {
        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        Result<Void> credentialsResult = authService.validateRegisterCredentials(userInsertDTO);
        if (!credentialsResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(credentialsResult.getErrorMessage()));
        }

        UserDTO userDTO = userService.createUser(userInsertDTO, Role.EMPLOYEE);

        String JWT = authService.ProcessRegister(userDTO);

        return ResponseEntity.ok(ResponseWrapper.ok(JWT,"User Successfully Registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<String>> login(@Valid @RequestBody LoginDTO loginDTO, BindingResult bindingResult) {
        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        Result<UserDTO> credentialsResult = authService.validateLoginCredentials(loginDTO);
        if (!credentialsResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(credentialsResult.getErrorMessage()));
        }

        String JWT = authService.ProcessLogin(credentialsResult.getData());

        return ResponseEntity.ok(ResponseWrapper.ok(JWT,"Login Successfully Completed"));
    }
}
