package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Dashboard.AdminDashboardDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Settings.SettingsDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AdminService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard() {
        AdminDashboardDTO adminDashboard = adminService.getAdminDashboard();

        return ResponseEntity.ok(adminDashboard);
    }


    @PutMapping("/settings")
    public ResponseEntity<ResponseWrapper<String>> updateSettings(@Valid @RequestBody SettingsDTO settingsDTO) {
        adminService.updateSettings(settingsDTO);

        return ResponseEntity.ok(ResponseWrapper.success(null, "Settings updated successfully"));
    }

    @GetMapping("/settings")
    public ResponseWrapper<SettingsDTO> getCurrentSettings() {
        SettingsDTO settingsDTO = adminService.getCurrentSettings();
        return ResponseWrapper.success(settingsDTO, "Current Settings Successfully Fetched");
    }

}

