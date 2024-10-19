package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.deletedAt IS NULL")
    Page<Expense> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Override
    @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Expense> findById(Long id);

    @Query("SELECT e FROM Expense e WHERE e.status = :status AND e.deletedAt IS NULL ORDER BY e.createdAt")
    Page<Expense> findByStatus(@Param("status") ExpenseStatus status, Pageable pageable);
}
