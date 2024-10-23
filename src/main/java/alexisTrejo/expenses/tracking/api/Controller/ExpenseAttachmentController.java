package alexisTrejo.expenses.tracking.api.Controller;

import alexisTrejo.expenses.tracking.api.DTOs.Attachements.AttachmentDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AttachmentService;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.ExpenseService;
import alexisTrejo.expenses.tracking.api.Utils.File.FileHandler;
import alexisTrejo.expenses.tracking.api.Utils.ResponseWrapper;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
public class ExpenseAttachmentController {
    private final ExpenseService expenseService;
    private final AttachmentService attachmentService;
    private final FileHandler fileHandler;

    @Autowired
    public ExpenseAttachmentController(ExpenseService expenseService,
                                       AttachmentService attachmentService,
                                       FileHandler fileHandler) {
        this.expenseService = expenseService;
        this.attachmentService = attachmentService;
        this.fileHandler = fileHandler;
    }

    @Operation(summary = "Add an attachment to an expense",
            description = "Uploads a file and associates it with a specific expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachment successfully added"),
            @ApiResponse(responseCode = "404", description = "Expense not found"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping("/{expenseId}/attachments")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseWrapper<Void>> addAttachment(
            @Parameter(description = "ID of the expense") @PathVariable Long expenseId,
            @Parameter(description = "File to be uploaded") @RequestParam(value = "file") MultipartFile file) throws IOException {

        Result<ExpenseDTO> expenseResult = expenseService.getExpenseById(expenseId);
        if (!expenseResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.notFound("Expense With Id(" + expenseId + ") Not Found"));
        }

        Result<String> fileUrlResult = fileHandler.uploadAttachmentFile(expenseResult.getData(), file);
        if (!fileUrlResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.badRequest(fileUrlResult.getErrorMessage()));
        }

        attachmentService.createAttachment(expenseId, fileUrlResult.getData());

        return ResponseEntity.ok(ResponseWrapper.ok(null, "Attachment Successfully Added to Expense With Id(" + expenseId + ")"));
    }

    @Operation(summary = "Get attachments by expense ID",
            description = "Fetches all attachments associated with a specific expense.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attachments successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Expense not found")
    })
    @GetMapping("/{expenseId}/attachments")
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<ResponseWrapper<List<AttachmentDTO>>> getAttachmentsByExpenseId(
            @Parameter(description = "ID of the expense") @PathVariable Long expenseId) {

        Result<List<AttachmentDTO>> expenseResult = attachmentService.getAttachmentsByExpenseId(expenseId);
        if (!expenseResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.notFound("Expense With Id(" + expenseId + ") Not Found"));
        }

        return ResponseEntity.ok(ResponseWrapper.ok(expenseResult.getData(), "Attachment Successfully Fetched Expense With Id(" + expenseId + ")"));
    }
}
