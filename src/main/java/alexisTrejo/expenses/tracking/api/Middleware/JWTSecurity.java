package alexisTrejo.expenses.tracking.api.Middleware;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Component
public class JWTSecurity {
    private final SecretKey secretKey;

    public JWTSecurity(@Value("${jwt.secret.key}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateToken(Long userId, String role) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
        claims.put("role", role);

        Date now = new Date();
        long validityDuration = 3600000; // 1 hour
        Date validity = new Date(now.getTime() + validityDuration);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            // Invalid signature/claims
            return null;
        }
    }

    public Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }

    public Claims getClaimsFromToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Remove "Bearer " prefix
            return validateToken(token);
        }
        return null;
    }

    public Long getUserIdFromToken(HttpServletRequest request) {
        Claims claims = getClaimsFromToken(request);
        if (claims != null) {
            return getUserId(claims);
        }
        return null;
    }

    public List<String> getRolesFromToken(HttpServletRequest request) {
        Claims claims = getClaimsFromToken(request);
        if (claims != null) {
            return getRoles(claims);
        }
        return null;
    }
}