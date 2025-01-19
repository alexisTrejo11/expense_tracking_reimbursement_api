package alexisTrejo.expenses.tracking.api.Config.Mailing;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.mail")
@Data
@NoArgsConstructor
public class MailProperties {
    private String host;
    private int port;
    private String username;
    private String password;
}