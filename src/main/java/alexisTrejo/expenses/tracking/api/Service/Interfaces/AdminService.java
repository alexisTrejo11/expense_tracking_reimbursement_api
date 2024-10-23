package alexisTrejo.expenses.tracking.api.Service.Interfaces;

import alexisTrejo.expenses.tracking.api.DTOs.Dashboard.AdminDashboardDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Settings.SettingsDTO;

public interface AdminService {
    AdminDashboardDTO getAdminDashboard();
    void updateSettings(SettingsDTO settingsDTO);
    SettingsDTO getCurrentSettings();
}
