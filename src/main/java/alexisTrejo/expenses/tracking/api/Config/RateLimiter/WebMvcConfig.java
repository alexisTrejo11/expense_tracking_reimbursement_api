package alexisTrejo.expenses.tracking.api.Config.RateLimiter;

import io.github.bucket4j.Bucket;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final Bucket bucket;

    public WebMvcConfig(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(bucket))
                .addPathPatterns("/**");
    }
}