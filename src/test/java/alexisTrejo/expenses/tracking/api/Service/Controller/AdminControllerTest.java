package alexisTrejo.expenses.tracking.api.Service.Controller;

import alexisTrejo.expenses.tracking.api.Controller.AdminController;
import alexisTrejo.expenses.tracking.api.DTOs.Dashboard.AdminDashboardDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Settings.SettingsDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AdminService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @InjectMocks
    private AdminController adminController;

    private AdminDashboardDTO testAdminDashboardDTO;
    private SettingsDTO testSettingsDTO;

    @BeforeEach
    void setUp() {
        testAdminDashboardDTO = new AdminDashboardDTO();
        testSettingsDTO = new SettingsDTO();
    }

    @Test
    void getAdminDashboard_ShouldReturnAdminDashboard() {
        // Arrange
        when(adminService.getAdminDashboard()).thenReturn(testAdminDashboardDTO);

        // Act
        ResponseEntity<AdminDashboardDTO> response = adminController.getAdminDashboard();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAdminDashboardDTO, response.getBody());
        verify(adminService).getAdminDashboard();
    }

    @Test
    void updateSettings_ShouldReturnSuccessMessage() {
        // Arrange
        doNothing().when(adminService).updateSettings(testSettingsDTO);

        // Act
        ResponseEntity<ResponseWrapper<String>> response = adminController.updateSettings(testSettingsDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Settings updated successfully", response.getBody().getMessage());
        verify(adminService).updateSettings(testSettingsDTO);
    }

    @Test
    void getCurrentSettings_ShouldReturnSettingsDTO() {
        // Arrange
        when(adminService.getCurrentSettings()).thenReturn(testSettingsDTO);

        // Act
        ResponseWrapper<SettingsDTO> response = adminController.getCurrentSettings();

        // Assert
        assertNotNull(response);
        assertEquals("Current Settings Successfully Fetched", response.getMessage());
        assertEquals(testSettingsDTO, response.getData());
        verify(adminService).getCurrentSettings();
    }
}

