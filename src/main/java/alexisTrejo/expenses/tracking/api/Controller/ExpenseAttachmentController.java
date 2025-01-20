package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Attachements.AttachmentDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AttachmentService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Utils.File.FileHandler;
import alexisTrejo.expenses.tracking.api.Utils.MessageGenerator;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/v1/api/employees/expenses")
@RequiredArgsConstructor
public class ExpenseAttachmentController {

    private final ExpenseService expenseService;
    private final AttachmentService attachmentService;
    private final FileHandler fileHandler;
    private final MessageGenerator message;

    @Operation(summary = "Add an attachment to an expense",
            description = "Uploads a file and associates it with a specific expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment successfully added"),
            @ApiResponse(responseCode = "404", description = "Expense not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/{expenseId}/attachments")
    public ResponseEntity<ResponseWrapper<Void>> addAttachment(
            @Parameter(description = "ID of the expense") @PathVariable Long expenseId,
            @Parameter(description = "File to be uploaded") @RequestParam(value = "file") MultipartFile file) throws IOException {

        ExpenseDTO expense = expenseService.getExpenseById(expenseId);
        if (expense == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Expense", "ID", expenseId));
        }

        Result<String> fileUrlResult = fileHandler.uploadAttachmentFile(expense, file);
        if (!fileUrlResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.badRequest(fileUrlResult.getErrorMessage()));
        }

        attachmentService.createAttachment(expenseId, fileUrlResult.getData());

        return ResponseEntity.ok(ResponseWrapper.success( "Attachment Successfully Added to Expense With Id [" + expenseId + "]"));
    }

    @Operation(summary = "Get attachments by expense ID",
            description = "Fetches all attachments associated with a specific expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachments successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @GetMapping("/{expenseId}/attachments")
    public ResponseEntity<ResponseWrapper<List<AttachmentDTO>>> getAttachmentsByExpenseId(@PathVariable Long expenseId) {
        List<AttachmentDTO> expenses = attachmentService.getAttachmentsByExpenseId(expenseId);
        if (expenses == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.notFound("Expense", "expenseId", expenseId));
        }

        return ResponseEntity.ok(ResponseWrapper.found(expenses, "Expenses", "expenseId", expenseId));
    }
}
