package alexisTrejo.expenses.tracking.api.Mappers;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-18T15:13:43-0600",
    comments = "version: 1.5.5.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.9.jar, environment: Java 17.0.11 (Amazon.com Inc.)"
)
@Component
public class ExpenseMapperImpl implements ExpenseMapper {

    @Override
    public Expense insertDtoToEntity(ExpenseInsertDTO expenseInsertDTO) {
        if ( expenseInsertDTO == null ) {
            return null;
        }

        Expense expense = new Expense();

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

        expenseDTO.setId( expense.getId() );
        expenseDTO.setAmount( expense.getAmount() );
        expenseDTO.setCategory( expense.getCategory() );
        expenseDTO.setDescription( expense.getDescription() );
        expenseDTO.setDate( expense.getDate() );
        expenseDTO.setReceiptUrl( expense.getReceiptUrl() );
        expenseDTO.setStatus( expense.getStatus() );
        expenseDTO.setApprovedBy( userToUserDTO( expense.getApprovedBy() ) );
        expenseDTO.setRejectionReason( expense.getRejectionReason() );

        return expenseDTO;
    }

    protected UserDTO userToUserDTO(User user) {
        if ( user == null ) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setEmail( user.getEmail() );
        userDTO.setFirstName( user.getFirstName() );
        userDTO.setLastName( user.getLastName() );
        userDTO.setDepartment( user.getDepartment() );
        userDTO.setId( user.getId() );
        userDTO.setPassword( user.getPassword() );
        userDTO.setRole( user.getRole() );
        userDTO.setActive( user.getActive() );

        return userDTO;
    }
}
