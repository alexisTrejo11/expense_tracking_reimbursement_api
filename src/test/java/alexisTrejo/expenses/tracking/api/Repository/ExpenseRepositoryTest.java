package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.User;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseCategory;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class ExpenseRepositoryTest {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public static Expense getTestExpense(User user) {
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(100.00);
        expense.setCategory(ExpenseCategory.TRAVEL);
        expense.setDescription("Business trip to New York");
        expense.setDate(LocalDate.now());
        expense.setReceiptUrl("http://example.com/receipt.png");
        expense.setStatus(ExpenseStatus.APPROVED);
        expense.setCreatedAt(LocalDateTime.now());
        expense.setUpdatedAt(LocalDateTime.now());
        return expense;
    }

    @Test
    public void whenSave_thenFindById() {
        User testUser = UserRepositoryTest.getTestUser();
        userRepository.save(testUser);

        Expense testExpense = getTestExpense(testUser);
        expenseRepository.save(testExpense);

        Optional<Expense> found = expenseRepository.findById(testExpense.getId());
        assertTrue(found.isPresent());
        assertEquals(testExpense.getDescription(), found.get().getDescription());
    }

    @Test
    public void whenUpdate_thenExpenseShouldBeUpdated() {
        User testUser = UserRepositoryTest.getTestUser();
        userRepository.save(testUser);

        Expense testExpense = getTestExpense(testUser);
        expenseRepository.save(testExpense);

        // Update the description
        testExpense.setDescription("Updated description");
        expenseRepository.save(testExpense);

        Optional<Expense> found = expenseRepository.findById(testExpense.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated description", found.get().getDescription());
    }

    @Test
    public void whenDelete_thenExpenseShouldNotBeFound() {
        User testUser = UserRepositoryTest.getTestUser();
        userRepository.save(testUser);

        Expense testExpense = getTestExpense(testUser);
        expenseRepository.save(testExpense);

        // Delete the expense
        expenseRepository.deleteById(testExpense.getId());

        Optional<Expense> found = expenseRepository.findById(testExpense.getId());
        assertFalse(found.isPresent());
    }

    @Test
    public void whenFindAll_thenAllExpensesShouldBeReturned() {
        User testUser = UserRepositoryTest.getTestUser();
        userRepository.save(testUser);

        Expense expense1 = getTestExpense(testUser);
        Expense expense2 = getTestExpense(testUser);
        expense2.setDescription("Another expense");

        expenseRepository.save(expense1);
        expenseRepository.save(expense2);

        List<Expense> expenses = expenseRepository.findAll();
        assertEquals(2, expenses.size());
    }
}
