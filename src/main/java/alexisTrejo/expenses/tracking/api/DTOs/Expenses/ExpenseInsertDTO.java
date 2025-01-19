package alexisTrejo.expenses.tracking.api.DTOs.Expenses;

import alexisTrejo.expenses.tracking.api.Utils.enums.ExpenseCategory;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
public class ExpenseInsertDTO {

    @JsonProperty("amount")
    @NotNull(message = "amount Is Obligatory")
    @Positive(message = "amount Must Be Positive")
    private Double amount;

    @JsonProperty("category")
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "category Is Obligatory")
    private ExpenseCategory category;

    @JsonProperty("description")
    @NotNull(message = "description Is Obligatory")
    @NotNull(message = "description Can't Be Empty")
    private String description;

    @JsonProperty("date")
    @NotNull(message = "date Is Obligatory")
    private LocalDate date;

    @JsonProperty("receipt_url")
    private String receiptUrl;

    @JsonProperty("rejection_reason")
    private String rejectionReason;
}
