package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReimbursementService {
    Result<ReimbursementDTO> getReimbursementById(Long reimbursementId);
    Result<Page<ReimbursementDTO>> getReimbursementByUserId(Long userId, Pageable pageable);
    Result<Void> createReimbursement(ReimbursementInsertDTO reimbursementInsertDTO, Long userId);

}
