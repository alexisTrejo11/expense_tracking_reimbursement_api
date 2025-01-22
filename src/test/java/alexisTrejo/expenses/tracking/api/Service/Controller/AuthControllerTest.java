package alexisTrejo.expenses.tracking.api.Service.Controller;

import alexisTrejo.expenses.tracking.api.Controller.AuthController;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.LoginDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Auth.UserInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.enums.Role;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AuthService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private UserInsertDTO testUserInsertDTO;
    private LoginDTO testLoginDTO;
    private UserDTO testUserDTO;

    private static final String TEST_JWT = "test-jwt";
    private static final String SUCCESS_MESSAGE = "Login Successfully Completed";

    @BeforeEach
    void setUp() {
        testUserInsertDTO = new UserInsertDTO();
        testUserInsertDTO.setEmail("test@example.com");
        testUserInsertDTO.setPassword("password");

        testLoginDTO = new LoginDTO();
        testLoginDTO.setEmail("test@example.com");
        testLoginDTO.setPassword("password");

        testUserDTO = new UserDTO();
        testUserDTO.setEmail("test@example.com");
    }

    @Test
    void registerEmployee_ShouldReturnSuccess() {
        // Arrange
        when(authService.validateRegisterCredentials(testUserInsertDTO)).thenReturn(Result.success());
        when(userService.createUser(testUserInsertDTO, Role.EMPLOYEE)).thenReturn(testUserDTO);
        when(authService.ProcessRegister(testUserDTO)).thenReturn(TEST_JWT);

        // Act
        ResponseEntity<ResponseWrapper<String>> response = authController.registerEmployee(testUserInsertDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_JWT, response.getBody().getData());
        verify(authService).validateRegisterCredentials(testUserInsertDTO);
        verify(userService).createUser(testUserInsertDTO, Role.EMPLOYEE);
        verify(authService).ProcessRegister(testUserDTO);
    }

    @Test
    void registerManager_ShouldReturnSuccess() {
        // Arrange
        when(authService.validateRegisterCredentials(testUserInsertDTO)).thenReturn(Result.success());
        when(userService.createUser(testUserInsertDTO, Role.MANAGER)).thenReturn(testUserDTO);
        when(authService.ProcessRegister(testUserDTO)).thenReturn(TEST_JWT);

        // Act
        ResponseEntity<ResponseWrapper<String>> response = authController.registerManager(testUserInsertDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_JWT, response.getBody().getData());
        verify(authService).validateRegisterCredentials(testUserInsertDTO);
        verify(userService).createUser(testUserInsertDTO, Role.MANAGER);
        verify(authService).ProcessRegister(testUserDTO);
    }

    @Test
    void registerAdmin_ShouldReturnSuccess() {
        // Arrange
        when(authService.validateRegisterCredentials(testUserInsertDTO)).thenReturn(Result.success());
        when(userService.createUser(testUserInsertDTO, Role.ADMIN)).thenReturn(testUserDTO);
        when(authService.ProcessRegister(testUserDTO)).thenReturn(TEST_JWT);

        // Act
        ResponseEntity<ResponseWrapper<String>> response = authController.registerAdmin(testUserInsertDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_JWT, response.getBody().getData());
        verify(authService).validateRegisterCredentials(testUserInsertDTO);
        verify(userService).createUser(testUserInsertDTO, Role.ADMIN);
        verify(authService).ProcessRegister(testUserDTO);
    }

    @Test
    void login_ShouldReturnSuccess() {
        // Arrange
        when(authService.validateLoginCredentials(testLoginDTO)).thenReturn(Result.success(testUserDTO));
        when(authService.processLogin(testUserDTO)).thenReturn(TEST_JWT);

        // Act
        ResponseEntity<ResponseWrapper<String>> response = authController.login(testLoginDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TEST_JWT, response.getBody().getData());
        assertEquals(SUCCESS_MESSAGE, response.getBody().getMessage());
        verify(authService).validateLoginCredentials(testLoginDTO);
        verify(authService).processLogin(testUserDTO);
    }

    @Test
    void login_ShouldReturnBadRequestOnInvalidCredentials() {
        // Arrange
        String errorMessage = "Invalid credentials";
        when(authService.validateLoginCredentials(testLoginDTO))
                .thenReturn(Result.error(errorMessage));

        // Act
        ResponseEntity<ResponseWrapper<String>> response = authController.login(testLoginDTO);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(errorMessage, response.getBody().getMessage());
        verify(authService).validateLoginCredentials(testLoginDTO);
        verify(authService, never()).processLogin(any());
    }
}

