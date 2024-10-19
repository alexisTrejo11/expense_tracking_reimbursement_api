package alexisTrejo.expenses.tracking.api.Models.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public enum ExpenseStatus {
    PENDING,
    APPROVED,
    REJECTED,
    REIMBURSED;

    public static Optional<ExpenseStatus> findStatus(String name) {
        return Arrays.stream(ExpenseStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(name))
                .findFirst();
    }
}