package alexisTrejo.expenses.tracking.api.Middleware;

import alexisTrejo.expenses.tracking.api.Utils.Exceptions.TokenValidationException;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTSecurity extends OncePerRequestFilter {

    private final SecretKey secretKey;

    @Autowired
    public JWTSecurity(@Value("${jwt.secret.key}") String secretKey) {
        this.secretKey = new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String token = getBearerTokenFromRequest(request);
        Claims claims = validateToken(token);

        String email = claims.getSubject();
        List<String> roles = getRoles(claims);

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }


    public String generateToken(Long userId, String role) {
        Claims claims = Jwts.claims().setSubject(userId.toString());
        String roleWithPrefix = "ROLE_" + role;
        claims.put("roles", List.of(roleWithPrefix));

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
        } catch (JwtException e) {
            throw new TokenValidationException("Token validation failed: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new TokenValidationException("An unexpected error occurred while validating the token.", e);
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
            String token = getBearerTokenFromRequest(request);
            return validateToken(token);
    }

    public Long getUserIdFromToken(HttpServletRequest request) {
        Claims claims = getClaimsFromToken(request);

        return getUserId(claims);
    }

    public List<String> getRolesFromToken(HttpServletRequest request) {
        Claims claims = getClaimsFromToken(request);

        return getRoles(claims);
    }

    private String getBearerTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
            return header.substring(7);
        } else {
            throw new TokenValidationException("Invalid header format");
        }
    }
}
