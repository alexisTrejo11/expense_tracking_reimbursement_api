package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AttachmentMapper.class})
public interface ExpenseMapper {

    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Expense insertDtoToEntity(ExpenseInsertDTO expenseInsertDTO);

    @Mapping(target = "approvedById", source = "approvedBy.id")
    @Mapping(target = "userId", source = "expense.user.id")
    @Mapping(target = "attachments", source = "expense.expenseAttachments")
    ExpenseDTO entityToDTO(Expense expense);
}
