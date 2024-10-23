package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Reimbursement.ReimbursementInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReimbursementMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "reimbursementDate", source = "reimbursementDate")
    Reimbursement insertDtoToEntity(ReimbursementInsertDTO expenseInsertDTO);

    @Mapping(target = "processedBy", source = "processedBy.id")
    @Mapping(target = "expense.approvedById", source = "expense.approvedBy.id")
    ReimbursementDTO entityToDTO(Reimbursement reimbursement);

}
