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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final ExpenseService expenseService;
    private final ReimbursementService reimbursementService;

    @Autowired
    public AdminController(AdminService adminService,
                           ExpenseService expenseService,
                           ReimbursementService reimbursementService) {
        this.adminService = adminService;
        this.expenseService = expenseService;
        this.reimbursementService = reimbursementService;
    }


    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardDTO> getAdminDashboard() {
        AdminDashboardDTO adminDashboard = adminService.getAdminDashboard();

        return ResponseEntity.ok(adminDashboard);
    }


    @PutMapping("/settings")
    public ResponseEntity<ResponseWrapper<String>> updateSettings(@Valid @RequestBody SettingsDTO settingsDTO,
                                                                  BindingResult bindingResult) {
        Result<Void> validationResult = Validations.validateDTO(bindingResult);
        if (!validationResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(validationResult.getErrorMessage()));
        }

        adminService.updateSettings(settingsDTO);

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Settings updated successfully"));
    }

    @GetMapping("/settings")
    public ResponseWrapper<SettingsDTO> getCurrentSettings() {
        SettingsDTO settingsDTO = adminService.getCurrentSettings();
        return ResponseWrapper.ok(settingsDTO, "Current Settings Successfully Fetched");
    }

}

