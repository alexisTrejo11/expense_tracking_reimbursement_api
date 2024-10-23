package alexisTrejo.expenses.tracking.api.Config.Swagger;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Expense Tracking API")
                        .description("Expense API REST that handles the expenses from a company")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Codmind")
                                .url("https://codmind.com")
                                .email("apis@codmind.com"))
                        .termsOfService("http://codmind.com/terms")
                );
    }
}