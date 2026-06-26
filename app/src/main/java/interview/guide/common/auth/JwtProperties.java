package interview.guide.common.auth;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.auth.jwt")
public class JwtProperties {

    /**
     * JWT 签名密钥（至少32字节）
     */
    private String secret;

    /**
     * 过期时间（分钟）
     */
    private long expireMinutes = 1440;

    @PostConstruct
    public void validate() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("APP_AUTH_JWT_SECRET 未配置，请在 .env 中设置");
        }
        if (secret.length() < 32) {
            throw new IllegalStateException("APP_AUTH_JWT_SECRET 长度不足32字节，请使用更长的密钥");
        }
        log.info("JWT 配置已加载，过期时间: {} 分钟", expireMinutes);
    }
}
