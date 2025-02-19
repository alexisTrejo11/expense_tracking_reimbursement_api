package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ReimbursementMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Utils.MessageGenerator;
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
    private final MessageGenerator message;

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
    public ReimbursementDTO createReimbursement(ReimbursementInsertDTO reimbursementInsertDTO, String email) {
        Reimbursement reimbursement = reimbursementMapper.insertDtoToEntity(reimbursementInsertDTO);
        setReimbursementRelationShips(reimbursementInsertDTO, reimbursement, email);

        reimbursementRepository.saveAndFlush(reimbursement);

        return reimbursementMapper.entityToDTO(reimbursement);
    }

    private void setReimbursementRelationShips(ReimbursementInsertDTO reimbursementInsertDTO,
                                               Reimbursement reimbursement,
                                               String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Long expenseId = reimbursementInsertDTO.getExpenseId();
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new EntityNotFoundException(message.notFoundPlain("Expense", expenseId)));

        reimbursement.setProcessedBy(user);
        reimbursement.setExpense(expense);

    }

    @Override
    public Result<Void> validate(ReimbursementInsertDTO insertDTO) {
        Expense expense = expenseRepository.findById(insertDTO.getExpenseId())
                .orElseThrow(() -> new EntityNotFoundException(message.notFoundPlain("Expense", insertDTO.getExpenseId())));

        if (expense.getStatus() != ExpenseStatus.APPROVED) {
            return Result.error("Only approved expenses can be reimbursement");
        }


        return Result.success();
    }
}
