package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.Models.Expense;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT e FROM Expense e WHERE e.user.id = :userId AND e.deletedAt IS NULL")
    Page<Expense> findByUserId(@Param("userId") Long userId, Pageable pageable);

    @Override
    @Query("SELECT e FROM Expense e WHERE e.id = :id AND e.deletedAt IS NULL")
    Optional<Expense> findById(Long id);

    @Query("SELECT e FROM Expense e WHERE e.status = :status AND e.deletedAt IS NULL ORDER BY e.createdAt")
    Page<Expense> findByStatus(@Param("status") ExpenseStatus status, Pageable pageable);

    @Query("SELECT new alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummaryDTO(SUM(e.amount), COUNT(e)) " +
            "FROM Expense e " +
            "WHERE e.status = :status " +
            "AND e.createdAt BETWEEN :startDate AND :endDate " +
            "AND e.deletedAt IS NULL")
    CompletableFuture<ExpenseSummaryDTO> getExpenseSummaryByStatusAndDateRange(
            @Param("status") ExpenseStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
