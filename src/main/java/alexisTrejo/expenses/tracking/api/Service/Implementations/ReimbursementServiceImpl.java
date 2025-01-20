package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ReimbursementMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Repository.ReimbursementRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ReimbursementService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReimbursementServiceImpl implements ReimbursementService {

    private final ReimbursementRepository reimbursementRepository;
    private final ReimbursementMapper reimbursementMapper;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    public ReimbursementDTO getReimbursementById(Long reimbursementId) {
        Optional<Reimbursement> optionalReimbursement = reimbursementRepository.findById(reimbursementId);

        return optionalReimbursement
                .map(reimbursementMapper::entityToDTO)
                .orElse(null);
    }

    @Override
    public Page<ReimbursementDTO> getReimbursementByUserId(Long userId, Pageable pageable) {
        boolean isUserExisting = userRepository.existsById(userId);
        if (!isUserExisting) {
            throw new EntityNotFoundException("User With Id [" + userId + "] Not Found");
        }

        Page<Reimbursement> reimbursementPage = reimbursementRepository.findByProcessedBy_Id(userId, pageable);

        return reimbursementPage.map(reimbursementMapper::entityToDTO);
    }

    @Override
    public Result<ReimbursementDTO> createReimbursement(ReimbursementInsertDTO reimbursementInsertDTO, String email) {
        Reimbursement reimbursement = reimbursementMapper.insertDtoToEntity(reimbursementInsertDTO);

        Result<Void> relationShipsResult = setReimbursementRelationShips(reimbursementInsertDTO, reimbursement, email);
        if (!relationShipsResult.isSuccess()) {
            return Result.error(relationShipsResult.getErrorMessage(), relationShipsResult.getStatus());
        }

        reimbursementRepository.save(reimbursement);

        return Result.success(reimbursementMapper.entityToDTO(reimbursement));
    }

    private Result<Void> setReimbursementRelationShips(ReimbursementInsertDTO reimbursementInsertDTO,
                                                       Reimbursement reimbursement,
                                                       String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new EntityNotFoundException("User not found") );

        Long expenseId = reimbursementInsertDTO.getExpenseId();
        reimbursement.setProcessedBy(user);

        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        if (optionalExpense.isEmpty()) {
            return Result.error("Expense With Id(" + expenseId + ") Not Found", HttpStatus.NOT_FOUND);
        }

        // Validate Expense
        ExpenseStatus expenseStatus = optionalExpense.get().getStatus();
        return switch (expenseStatus) {
            case REIMBURSED -> Result.error("Expense With Id(" + expenseId + ") Already Has Been Reimbursed", HttpStatus.BAD_REQUEST);
            case PENDING -> Result.error("Expense With Id(" + expenseId + ") Is Not Approved", HttpStatus.BAD_REQUEST);
            case REJECTED -> Result.error("Expense With Id(" + expenseId + ") Has Been Rejected", HttpStatus.BAD_REQUEST);
            case APPROVED -> {
                reimbursement.setExpense(optionalExpense.get());
                yield Result.success();
            }
        };
    }
}
