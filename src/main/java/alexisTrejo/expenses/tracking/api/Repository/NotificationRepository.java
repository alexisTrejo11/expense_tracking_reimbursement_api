package alexisTrejo.expenses.tracking.api.Repository;

import alexisTrejo.expenses.tracking.api.DTOs.Notification.NotificationDTO;
import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.Reimbursement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByUser_Id(Long userId, Pageable pageable);
}
