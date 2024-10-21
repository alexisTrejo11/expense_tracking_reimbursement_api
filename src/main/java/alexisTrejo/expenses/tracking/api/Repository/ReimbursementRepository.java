package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReimbursementRepository extends JpaRepository<Reimbursement, Long> {
    Page<Reimbursement> findByProcessedBy_Id(Long processedBy, Pageable pageable);
}
