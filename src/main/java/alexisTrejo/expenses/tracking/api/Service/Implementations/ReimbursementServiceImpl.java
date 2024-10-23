package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ReimbursementMapper;
import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import alexisTrejo.expenses.tracking.api.Repository.ReimbursementRepository;
import alexisTrejo.expenses.tracking.api.Repository.UserRepository;
import alexisTrejo.expenses.tracking.api.Service.DomainService.ReimbursementDomainService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ReimbursementService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ReimbursementServiceImpl implements ReimbursementService {

    private final ReimbursementRepository reimbursementRepository;
    private final ReimbursementMapper reimbursementMapper;
    private final UserRepository userRepository;
    private final ReimbursementDomainService reimbursementDomainService;

    @Autowired
    public ReimbursementServiceImpl(ReimbursementRepository reimbursementRepository,
                                    ReimbursementMapper reimbursementMapper, UserRepository userRepository, ReimbursementDomainService reimbursementDomainService) {
        this.reimbursementRepository = reimbursementRepository;
        this.reimbursementMapper = reimbursementMapper;
        this.userRepository = userRepository;
        this.reimbursementDomainService = reimbursementDomainService;
    }

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

        Result<Void> relationShipsResult = reimbursementDomainService.setReimbursementRelationShips(reimbursementInsertDTO, reimbursement, userId);
        if (!relationShipsResult.isSuccess()) {
            return Result.error(relationShipsResult.getErrorMessage(), relationShipsResult.getStatus());
        }

        reimbursementRepository.save(reimbursement);

        return Result.success(reimbursementMapper.entityToDTO(reimbursement));
    }

}
