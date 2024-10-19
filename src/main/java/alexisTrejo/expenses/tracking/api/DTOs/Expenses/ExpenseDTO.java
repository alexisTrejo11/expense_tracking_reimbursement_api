package alexisTrejo.expenses.tracking.api.DTOs.Expenses;

import alexisTrejo.expenses.tracking.api.DTOs.User.UserDTO;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseCategory;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
public class ExpenseDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("category")
    private ExpenseCategory category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("date")
    private LocalDate date;

    @JsonProperty("receipt_url")
    private String receiptUrl;

    @JsonProperty("status")
    private ExpenseStatus status;

    @JsonProperty("approvedBy")
    private UserDTO approvedBy;

    @JsonProperty("rejection_reason")
    private String rejectionReason;

}
