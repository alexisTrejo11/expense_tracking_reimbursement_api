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
    public Result<ReimbursementDTO> getReimbursementById(Long reimbursementId) {
        Optional<Reimbursement> optionalReimbursement = reimbursementRepository.findById(reimbursementId);
        return optionalReimbursement
                .map(reimbursement -> Result.success(reimbursementMapper.entityToDTO(reimbursement)))
                .orElseGet(() -> Result.error("Reimbursement With Id(" + reimbursementId + ") Not Found"));
    }

    @Override
    public Result<Page<ReimbursementDTO>> getReimbursementByUserId(Long userId, Pageable pageable) {
        boolean isUserExisting = userRepository.existsById(userId);
        if (!isUserExisting) {
            return Result.error("User With Id(" + userId + ") Not Found");
        }

        Page<Reimbursement> reimbursementPage = reimbursementRepository.findByProcessedBy_Id(userId, pageable);

        Page<ReimbursementDTO> reimbursementDTOPage = reimbursementPage.map(reimbursementMapper::entityToDTO);
        return Result.success(reimbursementDTOPage);
    }

    @Override
    public Result<ReimbursementDTO> createReimbursement(ReimbursementInsertDTO reimbursementInsertDTO, Long userId) {
        Reimbursement reimbursement = reimbursementMapper.insertDtoToEntity(reimbursementInsertDTO);

        Result<Void> relationShipsResult = setReimbursementRelationShips(reimbursementInsertDTO, reimbursement, userId);
        if (!relationShipsResult.isSuccess()) {
            return Result.error(relationShipsResult.getErrorMessage(), relationShipsResult.getStatus());
        }

        reimbursementRepository.save(reimbursement);

        return Result.success(reimbursementMapper.entityToDTO(reimbursement));
    }

    private Result<Void> setReimbursementRelationShips(ReimbursementInsertDTO reimbursementInsertDTO,
                                                       Reimbursement reimbursement,
                                                       Long userId) {
        Long expenseId = reimbursementInsertDTO.getExpenseId();
        reimbursement.setProcessedBy(new User(userId));

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
            default -> Result.error("Invalid Expense Status", HttpStatus.BAD_REQUEST);
        };
    }
}
