package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Dashboard.AdminDashboardDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Settings.SettingsDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ReimbursementService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AdminService;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import alexisTrejo.expenses.tracking.api.Utils.Validations;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard() {
        AdminDashboardDTO adminDashboard = adminService.getAdminDashboard();

        return ResponseEntity.ok(adminDashboard);
    }


    @PutMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResponseWrapper<String>> updateSettings(@Valid @RequestBody SettingsDTO settingsDTO) {
        adminService.updateSettings(settingsDTO);

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Settings updated successfully"));
    }

    @GetMapping("/settings")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseWrapper<SettingsDTO> getCurrentSettings() {
        SettingsDTO settingsDTO = adminService.getCurrentSettings();
        return ResponseWrapper.ok(settingsDTO, "Current Settings Successfully Fetched");
    }

}

