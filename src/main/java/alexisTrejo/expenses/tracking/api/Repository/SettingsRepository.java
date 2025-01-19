package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.AdminSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<AdminSettings, Long> {
}
