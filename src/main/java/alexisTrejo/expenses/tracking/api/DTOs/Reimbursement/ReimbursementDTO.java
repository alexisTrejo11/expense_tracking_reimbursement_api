package alexisTrejo.expenses.tracking.api.DTOs.Reimbursement;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor

public class ReimbursementDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("expense")
    private ExpenseDTO expense;

    @JsonProperty("processed_by_id")
    private Long processedBy;

    @JsonProperty("reimbursement_date")
    private LocalDate reimbursementDate;
}
