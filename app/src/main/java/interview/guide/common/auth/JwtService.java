package interview.guide.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT 服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT token
     *
     * @param id       用户ID
     * @param username 用户名
     * @return JWT token
     */
    public String generateToken(Long id, String username) {
        long now = System.currentTimeMillis();
        long expiration = now + jwtProperties.getExpireMinutes() * 60 * 1000;

        return Jwts.builder()
                .subject(String.valueOf(id))
                .claim("username", username)
                .issuedAt(new Date(now))
                .expiration(new Date(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 解析 JWT token
     *
     * @param token JWT token
     * @return Claims，解析失败返回 null
     */
    public Claims parse(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT 已过期: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
            return null;
        }
    }
}
