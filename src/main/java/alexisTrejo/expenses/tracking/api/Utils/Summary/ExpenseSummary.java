package alexisTrejo.expenses.tracking.api.Utils.Summary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExpenseSummary {
    @JsonProperty("summary_date_range")
    private String summaryDateRange;

    @JsonProperty("total_expenses")
    private int totalExpenses;

    @JsonProperty("apporved_expenses")
    private int approvedExpenses;

    @JsonProperty("reject_expenses")
    private int rejectedExpenses;

    @JsonProperty("pending_expenses")
    private int pendingExpenses;

    @JsonProperty("reimbursment_expenses")
    private int reimbursedExpenses;

    @JsonProperty("total_amount")
    private double totalAmount;

    @JsonProperty("approved_amount")
    private double approvedAmount;

    @JsonProperty("reject_amount")
    private double rejectedAmount;

    @JsonProperty("pending_amount")
    private double pendingAmount;

    @JsonProperty("reimbursed_amount")
    private double reimbursedAmount;

}
