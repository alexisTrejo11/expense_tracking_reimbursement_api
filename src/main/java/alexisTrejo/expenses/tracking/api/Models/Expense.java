package alexisTrejo.expenses.tracking.api.Models;

import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseCategory;
import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "expenses")
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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reimbursement_id")
    private Reimbursement reimbursement;

    @OneToMany(mappedBy = "expense", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ExpenseAttachment> expenseAttachments;

    @Column
    private String rejectionReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    public void setUserId(Long userId) {
        this.user = new User(userId);
    }

    public void setAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void setAsRejected(String rejectionReason) {
        this.status = ExpenseStatus.REJECTED;
        this.rejectionReason = rejectionReason;
    }

    public void addAttachment(ExpenseAttachment expenseAttachment) {
        if (this.expenseAttachments == null) {
            this.expenseAttachments = new ArrayList<>();
        }
        this.expenseAttachments.add(expenseAttachment);
    }

}

