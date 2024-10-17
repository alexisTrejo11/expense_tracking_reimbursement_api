package alexisTrejo.expenses.tracking.api.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class AdminSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double maxExpenseLimit;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "allowed_categories", joinColumns = @JoinColumn(name = "settings_id"))
    @Column(name = "category")
    private List<String> allowedCategories;

    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

