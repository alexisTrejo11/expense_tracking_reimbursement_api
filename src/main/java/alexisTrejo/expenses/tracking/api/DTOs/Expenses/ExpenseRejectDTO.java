package alexisTrejo.expenses.tracking.api.DTOs.Expenses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ExpenseRejectDTO {
    @JsonProperty("expense_id")
    private Long expenseId;

    @JsonProperty("rejecT_reason")
    private String rejectReason;
}
