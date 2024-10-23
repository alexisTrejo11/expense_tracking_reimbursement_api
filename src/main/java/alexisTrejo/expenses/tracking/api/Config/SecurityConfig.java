package alexisTrejo.expenses.tracking.api.Config;

import alexisTrejo.expenses.tracking.api.Middleware.JWTSecurity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JWTSecurity jwtSecurity;

    @Autowired
    public SecurityConfig(JWTSecurity jwtSecurity) {
        this.jwtSecurity = jwtSecurity;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v1/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/v1/api/employees/**").hasRole("EMPLOYEE")
                        .requestMatchers("/v1/api/manager/**").hasRole("MANAGER")
                        .requestMatchers("/v1/api/reimbursements/**").hasAnyRole("MANAGER", "FINANCIAL")
                        .requestMatchers("/v1/api/users/**").hasAnyRole("EMPLOYEE","MANAGER", "FINANCIAL", "ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtSecurity, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic ->
                        httpBasic
                                .authenticationEntryPoint((request, response, authException) -> {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.getWriter().write("Unauthorized");
                                })
                );

        return http.build();
    }
}
