package interview.guide.modules.market.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * 营销服务 RestClient 配置
 * <p>
 * 仿照 LlmProviderConfigService 的 RestClient 创建方式。
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class MarketRestClientConfig {

    private final MarketProperties marketProperties;

    @Bean(name = "marketRestClient")
    public RestClient marketRestClient() {
        HttpClientSettings settings = HttpClientSettings.defaults()
                .withConnectTimeout(Duration.ofMillis(marketProperties.getConnectTimeoutMs()))
                .withReadTimeout(Duration.ofMillis(marketProperties.getReadTimeoutMs()));

        return RestClient.builder()
                .baseUrl(marketProperties.getBaseUrl())
                .requestFactory(ClientHttpRequestFactoryBuilder.jdk().build(settings))
                .build();
    }
}
