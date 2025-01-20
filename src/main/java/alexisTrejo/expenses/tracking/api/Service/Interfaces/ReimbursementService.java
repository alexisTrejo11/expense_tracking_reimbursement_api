package alexisTrejo.expenses.tracking.api.Service.Interfaces;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReimbursementService {
    ReimbursementDTO getReimbursementById(Long reimbursementId);
    Page<ReimbursementDTO> getReimbursementByUserId(Long userId, Pageable pageable);
    Result<ReimbursementDTO> createReimbursement(ReimbursementInsertDTO reimbursementInsertDTO, String email);

}
