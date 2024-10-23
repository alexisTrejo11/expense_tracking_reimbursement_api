package alexisTrejo.expenses.tracking.api.Utils.Summary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ExpenseSummaryDTO {
    private Double totalAmount;
    private Long totalCount ;

    public ExpenseSummaryDTO() {
        this.totalCount = 0L;
        this.totalAmount = 0.0;
    }
}
