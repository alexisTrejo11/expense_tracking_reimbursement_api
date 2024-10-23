package alexisTrejo.expenses.tracking.api.Service.Implementations;

import alexisTrejo.expenses.tracking.api.Models.Notification;
import alexisTrejo.expenses.tracking.api.Models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl {

    @Autowired
    private JavaMailSender mailSender;

    @Async("taskExecutor")
    public void sendNotificationFromNotification(Notification notification) {
       User user = notification.getUser();

       String emailBody = createBodyFromNotification(notification, user);

        sendNotificationEmail(user.getEmail(), "Expense Notification", emailBody);
    }

    private String createBodyFromNotification(Notification notification, User user) {
        StringBuilder emailBody = new StringBuilder();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String message = notification.getMessage();

        emailBody.append("Hello, ").append(firstName).append(" ").append(lastName);
        emailBody.append(message);

        return emailBody.toString();
    }

    private void sendNotificationEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
