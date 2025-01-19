package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Dashboard.AdminDashboardDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Dashboard.DashboardStatsDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Settings.SettingsDTO;
import alexisTrejo.expenses.tracking.api.Models.AdminSettings;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Repository.SettingsRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final SettingsRepository settingsRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Cacheable("adminDashboardCache")
    public AdminDashboardDTO getAdminDashboard() {
        DashboardStatsDTO statsDTO = getDashboardStats();
        int pendingReimbursements = expenseRepository.countPendingReimbursement();

        return AdminDashboardDTO.builder()
                .totalExpenses(statsDTO.getTotalExpenses())
                .pendingExpenses(statsDTO.getPendingExpenses())
                .totalRejectedExpenses(statsDTO.getTotalRejectedExpenses())
                .totalApprovedExpenses(statsDTO.getTotalApprovedExpenses())
                .totalReimbursementExpenses(statsDTO.getTotalReimbursementExpenses())
                .pendingReimbursements(pendingReimbursements)
                .build();
    }

    @Override
    @Transactional
    public void updateSettings(SettingsDTO settingsDTO) {
        List<AdminSettings> adminSettings = settingsRepository.findAll();

        if (adminSettings.isEmpty()) {
            AdminSettings createSettings = new AdminSettings(settingsDTO.getMaxExpenseLimit(), settingsDTO.getAllowedCategories());
            settingsRepository.saveAndFlush(createSettings);
        } else {
            AdminSettings currentSettings  = adminSettings.get(adminSettings.size() + 1);

            currentSettings.setMaxExpenseLimit(settingsDTO.getMaxExpenseLimit());
            currentSettings.setAllowedCategories(settingsDTO.getAllowedCategories());

            settingsRepository.saveAndFlush(currentSettings);
        }
    }

    @Override
    @Cacheable("adminSettingsCache")
    public SettingsDTO getCurrentSettings() {
        List<AdminSettings> adminSettings = settingsRepository.findAll();
        AdminSettings currentSettings  = adminSettings.get(0);

        return new SettingsDTO(currentSettings.getMaxExpenseLimit(), currentSettings.getAllowedCategories());
    }

    private DashboardStatsDTO getDashboardStats() {
        List<Object[]> results = expenseRepository.getDashboardStatsRaw();
        if (results.isEmpty()) {
            return new DashboardStatsDTO(0, 0, 0, 0, 0); // Handle empty case
        }
        Object[] row = results.get(0);
        return new DashboardStatsDTO(
                ((Number) row[0]).intValue(),
                ((Number) row[1]).intValue(),
                ((Number) row[2]).intValue(),
                ((Number) row[3]).intValue(),
                ((Number) row[4]).intValue()
        );
    }
}
