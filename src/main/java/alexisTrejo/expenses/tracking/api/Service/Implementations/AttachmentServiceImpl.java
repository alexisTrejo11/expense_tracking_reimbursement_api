package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.DTOs.Attachements.AttachmentDTO;
import alexisTrejo.expenses.tracking.api.Mappers.AttachmentMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.ExpenseAttachment;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Service.Interfaces.AttachmentService;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {

    private final ExpenseRepository expenseRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    public List<AttachmentDTO> getAttachmentsByExpenseId(Long expenseId) {
            Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
             return optionalExpense.map(expense -> expense.getExpenseAttachments()
                     .stream()
                     .map(attachmentMapper::entityToDTO)
                     .toList())
                     .orElse(null);
    }

    @Override
    public void createAttachment(Long expenseId, String fileURL) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense Not Found"));

        ExpenseAttachment expenseAttachment = new ExpenseAttachment(expense, fileURL);

        expense.addAttachment(expenseAttachment);

        expenseRepository.saveAndFlush(expense);
    }
}
