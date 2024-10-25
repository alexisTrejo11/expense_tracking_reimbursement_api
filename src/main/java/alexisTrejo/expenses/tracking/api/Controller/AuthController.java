package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Auth.LoginDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import alexisTrejo.expenses.tracking.api.Models.enums.Role;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AuthService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.UserService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Validations;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("v1/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;
    private final JWTSecurity jwtSecurity;

    @Autowired
    public AuthController(UserService userService,
                          AuthService authService, JWTSecurity jwtSecurity) {
        this.userService = userService;
        this.authService = authService;
        this.jwtSecurity = jwtSecurity;
    }

    @Operation(summary = "Register an employee",
            description = "Registers a new employee and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input or credentials")
    })
    @PostMapping("/register-employee")
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

        return ResponseEntity.ok(ResponseWrapper.ok(JWT, "User With Role:" + Role.EMPLOYEE.name() + " Successfully Registered"));
    }

    @Operation(summary = "Register a manager",
            description = "Registers a new manager and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input or credentials")
    })
    @PostMapping("/register-manager")
    public ResponseEntity<ResponseWrapper<String>> registerManager(@Valid @RequestBody UserInsertDTO userInsertDTO,
                                                                   BindingResult bindingResult) {
        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        Result<Void> credentialsResult = authService.validateRegisterCredentials(userInsertDTO);
        if (!credentialsResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(credentialsResult.getErrorMessage()));
        }

        UserDTO userDTO = userService.createUser(userInsertDTO, Role.MANAGER);

        String JWT = authService.ProcessRegister(userDTO);
        return ResponseEntity.ok(ResponseWrapper.ok(JWT, "User With Role:" + Role.MANAGER.name() + " Successfully Registered"));
    }

    @Operation(summary = "Register an admin",
            description = "Registers a new admin and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid input or credentials")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register-admin")
    public ResponseEntity<ResponseWrapper<String>> registerAdmin(@Valid @RequestBody UserInsertDTO userInsertDTO,
                                                                 BindingResult bindingResult) {
        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        Result<Void> credentialsResult = authService.validateRegisterCredentials(userInsertDTO);
        if (!credentialsResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(credentialsResult.getErrorMessage()));
        }

        UserDTO userDTO = userService.createUser(userInsertDTO, Role.ADMIN);

        String JWT = authService.ProcessRegister(userDTO);
        return ResponseEntity.ok(ResponseWrapper.ok(JWT, "User With Role:" + Role.ADMIN.name() + " Successfully Registered"));
    }

    @Operation(summary = "Login user",
            description = "Logs in a user and returns a JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successfully completed"),
            @ApiResponse(responseCode = "400", description = "Bad Request: Invalid login credentials")
    })
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

        return ResponseEntity.ok(ResponseWrapper.ok(JWT, "Login Successfully Completed"));
    }

    @GetMapping("/claims")
    public Object claims(HttpServletRequest request) {
        return jwtSecurity.getClaimsFromToken(request);
    }
}
