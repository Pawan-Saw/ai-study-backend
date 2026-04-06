package ai_study_backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getKey() {
        // ✅ UTF-8 encoding use karo
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // ✅ Agar key short hai toh pad karo
        if (keyBytes.length < 64) {
            byte[] padded = new byte[64];
            System.arraycopy(keyBytes, 0, padded, 0, keyBytes.length);
            return Keys.hmacShaKeyFor(padded);
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        return Jwts.builder()
            .setSubject(email)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getKey())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public boolean isValid(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
            // ✅ Expiry check
            return claims.getExpiration().after(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT Expired: " + e.getMessage());
            return false;
        } catch (SignatureException e) {
            System.out.println("JWT Signature invalid: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("JWT Error: " + e.getMessage());
            return false;
        }
    }
}