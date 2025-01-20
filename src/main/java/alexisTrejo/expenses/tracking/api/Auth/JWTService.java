package alexisTrejo.expenses.tracking.api.Auth;

import alexisTrejo.expenses.tracking.api.Utils.Exceptions.SecurityExceptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.core.userdetails.UserDetails;
import jakarta.servlet.http.HttpServletRequest;

import java.time.Instant;
import java.util.Map;
import java.util.function.Consumer;

public class JWTService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    @Value("${jwt.expiration.ms}")
    private long jwtExpirationMs;

    public JWTService(JwtEncoder jwtEncoder,
                      JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String generateToken(UserDetails userDetails, Consumer<Map<String, Object>> extraClaims) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("alexisTrejo.expenses.tracking")
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtExpirationMs))
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(auth -> auth.getAuthority().replace("ROLE_", "")) // Opcional: eliminar prefijo "ROLE_"
                        .toList())
                .claims(extraClaims)
                .build();

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(userDetails, claims -> {});
    }

    public String extractUsername(String token) {
        try {
            Jwt decodedJwt = this.jwtDecoder.decode(token);
            return decodedJwt.getSubject();
        } catch (JwtException e) {
            throw new SecurityExceptions.JwtTokenInvalidException();
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Jwt decodedJwt = this.jwtDecoder.decode(token);
            String username = decodedJwt.getSubject();
            return username.equals(userDetails.getUsername()) && !isTokenExpired(decodedJwt);
        } catch (JwtException e) {
            throw new SecurityExceptions.JwtTokenInvalidException();
        }
    }

    private boolean isTokenExpired(Jwt jwt) {
        Instant expiration = jwt.getExpiresAt();
        return expiration != null && expiration.isBefore(Instant.now());
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new SecurityExceptions.JwtTokenMissingException();
        }
        return authHeader.substring(7);
    }

    public String getEmailFromRequest(HttpServletRequest request) {
        String token = getJwtFromRequest(request);
        return extractUsername(token);
    }
}
