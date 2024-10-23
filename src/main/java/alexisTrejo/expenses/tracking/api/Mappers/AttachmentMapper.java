package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Attachements.AttachmentDTO;
import alexisTrejo.expenses.tracking.api.Models.ExpenseAttachment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {

    @Mapping(target = "expense", ignore = true)
    AttachmentDTO entityToDTO(ExpenseAttachment expenseAttachment);
}
