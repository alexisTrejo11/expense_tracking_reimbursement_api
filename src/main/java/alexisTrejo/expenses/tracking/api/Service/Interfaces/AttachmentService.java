package alexisTrejo.expenses.tracking.api.Service.Interfaces;

import alexisTrejo.expenses.tracking.api.DTOs.Attachements.AttachmentDTO;
import alexisTrejo.expenses.tracking.api.Utils.Result;

import java.util.List;

public interface AttachmentService {

    Result<List<AttachmentDTO>> getAttachmentsByExpenseId(Long expenseId);
    void createAttachment(Long expenseId, String fileURL);
}
