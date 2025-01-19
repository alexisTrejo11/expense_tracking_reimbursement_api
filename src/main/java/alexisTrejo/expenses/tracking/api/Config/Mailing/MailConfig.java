package alexisTrejo.expenses.tracking.api.Config.Mailing;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    @Autowired
    private alexisTrejo.expenses.tracking.api.Config.Mailing.MailProperties mailProperties;

    @Bean
    public JavaMailSender javaMailSender() {
        System.out.println("SMTP Host: " + mailProperties.getHost());
        System.out.println("SMTP User: " + mailProperties.getUsername());
        System.out.println("SMTP Password: " + mailProperties.getPassword());

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailProperties.getHost());
        mailSender.setPort(mailProperties.getPort());
        mailSender.setUsername(mailProperties.getUsername());
        mailSender.setPassword(mailProperties.getPassword());

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }
}
