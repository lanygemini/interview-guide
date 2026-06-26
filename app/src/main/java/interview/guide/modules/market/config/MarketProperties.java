package interview.guide.modules.market.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 大营销平台对接配置
 */
@Slf4j
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.market")
public class MarketProperties {

    /**
     * 营销服务基础地址（仅 host:port，/api/v1/raffle/activity 作为 client 内常量前缀）
     */
    private String baseUrl = "http://localhost:8091";

    /**
     * 默认活动ID（前端未选择时的兜底）
     */
    private String activityId = "100301";

    /**
     * 可选活动列表
     */
    private List<ActivityConfig> activities = new ArrayList<>();

    /**
     * 连接超时（毫秒）
     */
    private int connectTimeoutMs = 3000;

    /**
     * 读取超时（毫秒）
     */
    private int readTimeoutMs = 8000;

    @Getter
    @Setter
    public static class ActivityConfig {
        private String id;
        private String name;
    }

    @PostConstruct
    public void logConfig() {
        log.info("营销平台配置已加载: baseUrl={}, 默认activityId={}, 可选活动={}",
                baseUrl, activityId, activities.size());
    }
}
