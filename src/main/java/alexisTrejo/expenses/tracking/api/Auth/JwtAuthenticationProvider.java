package alexisTrejo.expenses.tracking.api.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

@RequiredArgsConstructor
@Configuration
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JWTService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String token = (String) authentication.getCredentials();

        if (jwtService.validateToken(token)) {
            String username = jwtService.extractUsername(token);

            UserDetails userDetails = new User(username, "", new ArrayList<>());

            return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
        } else {
            throw new AuthenticationException("Invalid Token") {};
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
