package alexisTrejo.expenses.tracking.api.DTOs.Expenses;

import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseCategory;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @JsonProperty("status")
    @Enumerated(EnumType.ORDINAL)
    @NotNull(message = "status Is Obligatory")
    private ExpenseStatus status;

    @JsonProperty("approved_by_id")
    private Long approvedById;

    @JsonProperty("rejection_reason")
    private String rejectionReason;
}
