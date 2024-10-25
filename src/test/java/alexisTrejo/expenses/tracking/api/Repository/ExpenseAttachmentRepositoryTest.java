package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.ExpenseAttachment;
import alexisTrejo.expenses.tracking.api.Models.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DataJpaTest
public class ExpenseAttachmentRepositoryTest {

    @Autowired
    private ExpenseAttachmentRepository expenseAttachmentRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    public ExpenseAttachment getTestAttachment(Expense expense) {
        return new ExpenseAttachment(expense, "http://example.com/attachment.png");
    }

    @Test
    public void whenSave_thenFindById() {
        User testUser = UserRepositoryTest.getTestUser();
        userRepository.save(testUser);

        Expense testExpense = ExpenseRepositoryTest.getTestExpense(testUser);
        expenseRepository.save(testExpense);

        ExpenseAttachment testAttachment = getTestAttachment(testExpense);
        expenseAttachmentRepository.save(testAttachment);

        Optional<ExpenseAttachment> found = expenseAttachmentRepository.findById(testAttachment.getId());
        assertTrue(found.isPresent());
        assertEquals(testAttachment.getAttachmentUrl(), found.get().getAttachmentUrl());
    }

    @Test
    public void whenUpdate_thenAttachmentShouldBeUpdated() {
        User testUser = UserRepositoryTest.getTestUser();
        userRepository.save(testUser);

        Expense testExpense = ExpenseRepositoryTest.getTestExpense(testUser);
        expenseRepository.save(testExpense);

        ExpenseAttachment testAttachment = getTestAttachment(testExpense);
        expenseAttachmentRepository.save(testAttachment);

        // Update the attachment URL
        testAttachment.setAttachmentUrl("http://example.com/updated_attachment.png");
        expenseAttachmentRepository.save(testAttachment);

        Optional<ExpenseAttachment> found = expenseAttachmentRepository.findById(testAttachment.getId());
        assertTrue(found.isPresent());
        assertEquals("http://example.com/updated_attachment.png", found.get().getAttachmentUrl());
    }

    @Test
    public void whenDelete_thenAttachmentShouldNotBeFound() {
        User testUser = UserRepositoryTest.getTestUser();
        userRepository.save(testUser);

        Expense testExpense = ExpenseRepositoryTest.getTestExpense(testUser);
        expenseRepository.save(testExpense);

        ExpenseAttachment testAttachment = getTestAttachment(testExpense);
        expenseAttachmentRepository.save(testAttachment);

        // Delete the attachment
        expenseAttachmentRepository.deleteById(testAttachment.getId());

        Optional<ExpenseAttachment> found = expenseAttachmentRepository.findById(testAttachment.getId());
        assertFalse(found.isPresent());
    }
}
