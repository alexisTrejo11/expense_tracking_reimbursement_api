package alexisTrejo.expenses.tracking.api.Middleware;

import alexisTrejo.expenses.tracking.api.Utils.Result;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    public Result<Claims> validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return Result.success(claims);
        } catch (ExpiredJwtException e) {
            // JWT token has expired
            return Result.error("Token expired at " + e.getClaims().getExpiration());
        } catch (SignatureException e) {
            // Invalid signature/claims
            return Result.error("Invalid Token");
        } catch (Exception e) {
            // Any other exception related to token parsing
            return Result.error("Token parsing error");
        }
    }


    public Long getUserId(Claims claims) {
        return Long.parseLong(claims.getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(Claims claims) {
        return (List<String>) claims.get("roles");
    }

    public Result<Claims> getClaimsFromToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            String token = header.substring(7); // Remove "Bearer " prefix

            Result<Claims> claimsResult = validateToken(token);
            if (!claimsResult.isSuccess()) {
                return Result.error(claimsResult.getErrorMessage());
            }

            return Result.success(claimsResult.getData());
        }
        return Result.error("Invalid Header Format");
    }

    public Result<Long> getUserIdFromToken(HttpServletRequest request) {
        Result<Claims> claimsResult = getClaimsFromToken(request);
        if (!claimsResult.isSuccess()) {
            return Result.error(claimsResult.getErrorMessage());
        }
        return Result.success(getUserId(claimsResult.getData()));
    }

    public Result<List<String>> getRolesFromToken(HttpServletRequest request) {
        Result<Claims> claimsResult = getClaimsFromToken(request);
        if (!claimsResult.isSuccess()) {
            return Result.error(claimsResult.getErrorMessage());
        }
        return Result.success(getRoles(claimsResult.getData()));
    }
}