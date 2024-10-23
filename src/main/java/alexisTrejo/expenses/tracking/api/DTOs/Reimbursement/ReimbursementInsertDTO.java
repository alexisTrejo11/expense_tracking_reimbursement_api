package alexisTrejo.expenses.tracking.api.DTOs.Reimbursement;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor

public class ReimbursementInsertDTO {
    @JsonProperty("expense_id")
    @NotNull(message = "expense_id is obligatory")
    @Positive(message = "expense_id must be positive")
    private Long expenseId;

    @JsonProperty("reimbursement_date")
    @NotNull(message = "reimbursement_date is obligatory")
    private LocalDate reimbursementDate;
}
