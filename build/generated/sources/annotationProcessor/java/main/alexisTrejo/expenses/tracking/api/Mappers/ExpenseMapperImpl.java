package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Attachements.AttachmentDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.ExpenseAttachment;
import alexisTrejo.expenses.tracking.api.Models.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-01-21T18:43:16-0600",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.jar, environment: Java 17.0.12 (JetBrains s.r.o.)"
)
@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    public Expense insertDtoToEntity(ExpenseInsertDTO expenseInsertDTO) {
        if ( expenseInsertDTO == null ) {
            return null;
        }

        Expense expense = new Expense();

        expense.setAmount( expenseInsertDTO.getAmount() );
        expense.setCategory( expenseInsertDTO.getCategory() );
        expense.setDescription( expenseInsertDTO.getDescription() );
        expense.setDate( expenseInsertDTO.getDate() );
        expense.setReceiptUrl( expenseInsertDTO.getReceiptUrl() );
        expense.setRejectionReason( expenseInsertDTO.getRejectionReason() );

        expense.setCreatedAt( java.time.LocalDateTime.now() );
        expense.setUpdatedAt( java.time.LocalDateTime.now() );

        return expense;
    }

    @Override
    public ExpenseDTO entityToDTO(Expense expense) {
        if ( expense == null ) {
            return null;
        }

        ExpenseDTO expenseDTO = new ExpenseDTO();

        expenseDTO.setApprovedById( expenseApprovedById( expense ) );
        expenseDTO.setUserId( expenseUserId( expense ) );
        expenseDTO.setAttachments( expenseAttachmentListToAttachmentDTOList( expense.getExpenseAttachments() ) );
        expenseDTO.setId( expense.getId() );
        expenseDTO.setAmount( expense.getAmount() );
        expenseDTO.setCategory( expense.getCategory() );
        expenseDTO.setDescription( expense.getDescription() );
        expenseDTO.setDate( expense.getDate() );
        expenseDTO.setReceiptUrl( expense.getReceiptUrl() );
        expenseDTO.setStatus( expense.getStatus() );
        expenseDTO.setRejectionReason( expense.getRejectionReason() );

        return expenseDTO;
    }

    private Long expenseApprovedById(Expense expense) {
        if ( expense == null ) {
            return null;
        }
        User approvedBy = expense.getApprovedBy();
        if ( approvedBy == null ) {
            return null;
        }
        Long id = approvedBy.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long expenseUserId(Expense expense) {
        if ( expense == null ) {
            return null;
        }
        User user = expense.getUser();
        if ( user == null ) {
            return null;
        }
        Long id = user.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    protected List<AttachmentDTO> expenseAttachmentListToAttachmentDTOList(List<ExpenseAttachment> list) {
        if ( list == null ) {
            return null;
        }

        List<AttachmentDTO> list1 = new ArrayList<AttachmentDTO>( list.size() );
        for ( ExpenseAttachment expenseAttachment : list ) {
            list1.add( attachmentMapper.entityToDTO( expenseAttachment ) );
        }

        return list1;
    }
}
