package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async("taskExecutor")
    public void sendEmailFromNotification(Notification notification) {
        User user = notification.getUser();
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            log.error("Notification does not have a valid user or email.");
            return;
        }

        sendNotificationEmail(notification);
    }

    private void sendNotificationEmail(Notification notification) {
        Context context = new Context();
        User user = notification.getUser();

        context.setVariable("name", user.getFirstName() + " " + user.getLastName());
        context.setVariable("subject", "Expense Notification");
        context.setVariable("message", notification.getMessage());

        String htmlContent = templateEngine.process("notification", context);

        // Prepare and send email
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(user.getEmail());
            helper.setSubject("Expense Notification");
            helper.setText(htmlContent, true);
        };

        mailSender.send(preparator);
    }
}
