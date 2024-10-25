package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.ExpenseAttachment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseAttachmentRepository extends JpaRepository<ExpenseAttachment, Long> {
}
