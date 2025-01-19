package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.Models.enums.ExpenseStatus;
import alexisTrejo.expenses.tracking.api.Repository.ExpenseRepository;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummary;
import alexisTrejo.expenses.tracking.api.Utils.Summary.ExpenseSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ExpenseSummaryService {

    private final ExpenseRepository expenseRepository;

    @Async
    public CompletableFuture<ExpenseSummary> generateExpenseSummary(LocalDateTime startDate, LocalDateTime endDate) {
        CompletableFuture<ExpenseSummaryDTO> rejectedSummaryFuture = expenseRepository.getExpenseSummaryByStatusAndDateRange(ExpenseStatus.REJECTED, startDate, endDate);
        CompletableFuture<ExpenseSummaryDTO> approvedSummaryFuture = expenseRepository.getExpenseSummaryByStatusAndDateRange(ExpenseStatus.APPROVED, startDate, endDate);
        CompletableFuture<ExpenseSummaryDTO> pendingSummaryFuture = expenseRepository.getExpenseSummaryByStatusAndDateRange(ExpenseStatus.PENDING, startDate, endDate);
        CompletableFuture<ExpenseSummaryDTO> reimbursedSummaryFuture = expenseRepository.getExpenseSummaryByStatusAndDateRange(ExpenseStatus.REIMBURSED, startDate, endDate);

        CompletableFuture.allOf(rejectedSummaryFuture, approvedSummaryFuture, pendingSummaryFuture, reimbursedSummaryFuture).join();

        ExpenseSummaryDTO rejectedSummaryDTO = getOrDefault(rejectedSummaryFuture.join());
        ExpenseSummaryDTO approvedSummaryDTO = getOrDefault(approvedSummaryFuture.join());
        ExpenseSummaryDTO pendingSummaryDTO = getOrDefault(pendingSummaryFuture.join());
        ExpenseSummaryDTO reimbursedSummaryDTO = getOrDefault(reimbursedSummaryFuture.join());

        // Extract counts and amounts from the DTOs
        int approvedCount = Math.toIntExact(approvedSummaryDTO.getTotalCount());
        int reimbursedCount = Math.toIntExact(reimbursedSummaryDTO.getTotalCount());
        int pendingCount = Math.toIntExact(pendingSummaryDTO.getTotalCount());
        int rejectCount = Math.toIntExact(rejectedSummaryDTO.getTotalCount());

        Double reimbursedAmount = reimbursedSummaryDTO.getTotalAmount();
        Double pendingAmount = pendingSummaryDTO.getTotalAmount();
        Double approvedAmount = approvedSummaryDTO.getTotalAmount();
        Double rejectAmount = rejectedSummaryDTO.getTotalAmount();

        // Create a summary date range string
        String summaryDateRange = startDate + " to " + endDate;

        // Calculate total expenses and total amount
        int totalExpenses = reimbursedCount + pendingCount + approvedCount + rejectCount;
        double totalAmount = reimbursedAmount + pendingAmount + approvedAmount + rejectAmount;

        return CompletableFuture.completedFuture(ExpenseSummary.builder()
                .summaryDateRange(summaryDateRange)
                .totalExpenses(totalExpenses)
                .approvedExpenses(approvedCount)
                .rejectedExpenses(rejectCount)
                .pendingExpenses(pendingCount)
                .reimbursedExpenses(reimbursedCount)
                .totalAmount(totalAmount)
                .approvedAmount(approvedAmount)
                .rejectedAmount(rejectAmount)
                .pendingAmount(pendingAmount)
                .reimbursedAmount(reimbursedAmount)
                .build());
    }

    private ExpenseSummaryDTO getOrDefault(ExpenseSummaryDTO dto) {
        return new ExpenseSummaryDTO(
                dto != null && dto.getTotalAmount() != null ? dto.getTotalAmount() : 0.0,
                dto != null ? dto.getTotalCount() : 0L
        );
    }
}
