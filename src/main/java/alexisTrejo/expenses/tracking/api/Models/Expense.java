package alexisTrejo.expenses.tracking.api.Models;

import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseCategory;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "expenses")
@Entity
@NoArgsConstructor
@Data
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseCategory category;

    @Column(nullable = false, length = 500)
    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String receiptUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpenseStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;  // Manager who approved the expense

    @Column
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public void setUserRelationShipIds(Long userId, Long approvedBy) {
        this.user = new User(userId);

        if (approvedBy != null) {
            this.approvedBy = new User(approvedBy);
        }
    }

    public void setAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
}

