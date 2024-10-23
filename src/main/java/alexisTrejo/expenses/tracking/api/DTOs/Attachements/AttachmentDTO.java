package alexisTrejo.expenses.tracking.api.DTOs.Attachements;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AttachmentDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("expense")
    private ExpenseDTO expense;

    @JsonProperty("attachment_url")
    private String attachmentUrl;

    @JsonProperty("uploaded_at")
    private LocalDateTime uploadedAt;
}
