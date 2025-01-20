package alexisTrejo.expenses.tracking.api.Config.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class SecurityExceptionHandlers {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Component
    public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response,
                             AuthenticationException authException) throws IOException {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            String message = "Authentication failed";
            if (authException instanceof InvalidBearerTokenException) {
                message = "Invalid or expired JWT token";
            }

            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
            body.put("error", "Unauthorized");
            body.put("message", message);
            body.put("path", request.getServletPath());
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("details", authException.getMessage());

            objectMapper.writeValue(response.getOutputStream(), body);
        }
    }

    @Component
    public class CustomAccessDeniedHandler implements AccessDeniedHandler {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response,
                           AccessDeniedException accessDeniedException) throws IOException {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            Map<String, Object> body = new HashMap<>();
            body.put("status", HttpServletResponse.SC_FORBIDDEN);
            body.put("error", "Forbidden");
            body.put("message", "Insufficient permissions to access this resource");
            body.put("path", request.getServletPath());
            body.put("timestamp", LocalDateTime.now().toString());
            body.put("details", accessDeniedException.getMessage());
            body.put("required_roles", "Contact administrator for required roles");

            objectMapper.writeValue(response.getOutputStream(), body);
        }
    }


}