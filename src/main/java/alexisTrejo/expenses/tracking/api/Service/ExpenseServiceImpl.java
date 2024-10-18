package alexisTrejo.expenses.tracking.api.Service;

import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseDTO;
import alexisTrejo.expenses.tracking.api.DTOs.Expenses.ExpenseInsertDTO;
import alexisTrejo.expenses.tracking.api.Mappers.ExpenseMapper;
import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final ExpenseMapper expenseMapper;

    @Autowired
    public ExpenseServiceImpl(ExpenseRepository expenseRepository, ExpenseMapper expenseMapper) {
        this.expenseRepository = expenseRepository;
        this.expenseMapper = expenseMapper;
    }

    @Override
    public Result<ExpenseDTO> GetExpenseById(Long expenseId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        return optionalExpense
                .map(expense -> Result.success(expenseMapper.entityToDTO(expense)))
                .orElseGet(() -> Result.error("Expense With Id(" + expenseId + ") not found"));
    }

    @Override
    public Page<ExpenseDTO> GetExpenseByUserId(Long userId, Pageable pageable) {
        Page<Expense> expenses = expenseRepository.findByUserId(userId, pageable);
        return expenses.map(expenseMapper::entityToDTO);
    }


    @Override
    @Transactional
    public void CreateExpense(ExpenseInsertDTO expenseInsertDTO, Long userId) {
        Expense expense = expenseMapper.insertDtoToEntity(expenseInsertDTO);

        expense.setUserRelationShipIds(userId, expenseInsertDTO.getApprovedById());
        expenseRepository.saveAndFlush(expense);
    }

    @Override
    @Transactional
    public Result<Void> SoftDeleteExpenseById(Long expenseId) {
        Optional<Expense> optionalExpense = expenseRepository.findById(expenseId);
        return optionalExpense
                .map(expense -> {
                    expense.setAsDeleted();
                    expenseRepository.saveAndFlush(expense);
                    return Result.success();
                })
                .orElseGet(() -> Result.error("Expense With Id(" + expenseId + ") not found"));
    }
}
