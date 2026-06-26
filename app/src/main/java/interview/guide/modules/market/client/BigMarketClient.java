package interview.guide.modules.market.client;

import interview.guide.common.exception.BusinessException;
import interview.guide.common.exception.ErrorCode;
import interview.guide.modules.market.client.dto.AccountQueryRequestPayload;
import interview.guide.modules.market.client.dto.BigMarketResponse;
import interview.guide.modules.market.client.dto.DrawAwardData;
import interview.guide.modules.market.client.dto.DrawRequestPayload;
import interview.guide.modules.market.client.dto.RaffleAwardListRequestDTO;
import interview.guide.modules.market.client.dto.RaffleAwardListResponseDTO;
import interview.guide.modules.market.client.dto.SkuProductData;
import interview.guide.modules.market.client.dto.UserActivityAccountData;
import interview.guide.modules.market.client.dto.UserCreditAccountData;
import interview.guide.modules.market.config.MarketProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.math.BigDecimal;
import java.util.List;

/**
 * 大营销平台 HTTP 客户端
 * <p>
 * 内部 catch 网络/超时/服务端异常，转成 13xxx BusinessException，
 * 防止被全局 GlobalExceptionHandler 误判为 AI 服务错误（7xxx）。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BigMarketClient {

    private static final String PREFIX = "/api/v1/raffle/activity/";
    private static final String STRATEGY_PREFIX = "/api/v1/raffle/strategy/";
    private static final String SUCCESS_CODE = "0000";

    private final RestClient marketRestClient;
    private final MarketProperties marketProperties;

    /**
     * 抽奖
     */
    public DrawAwardData draw(String userId, String activityId) {
        DrawRequestPayload body = new DrawRequestPayload(userId, activityId);
        BigMarketResponse<DrawAwardData> resp = post("draw", body,
                new ParameterizedTypeReference<BigMarketResponse<DrawAwardData>>() {});
        return resp.getData();
    }

    /**
     * 查询奖品列表
     */
    public List<RaffleAwardListResponseDTO> queryRaffleAwardList(String userId, Long activityId) {
        RaffleAwardListRequestDTO body = new RaffleAwardListRequestDTO(userId, activityId);
        try {
            BigMarketResponse<List<RaffleAwardListResponseDTO>> resp = marketRestClient.post()
                    .uri(STRATEGY_PREFIX + "query_raffle_award_list")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .retrieve()
                    .body(new ParameterizedTypeReference<BigMarketResponse<List<RaffleAwardListResponseDTO>>>() {});

            if (resp == null) {
                throw new BusinessException(ErrorCode.MARKET_RESPONSE_ERROR, "营销服务返回空响应");
            }
            if (!SUCCESS_CODE.equals(resp.getCode())) {
                log.warn("营销服务返回非成功码: path=query_raffle_award_list, code={}, info={}", resp.getCode(), resp.getInfo());
                throw new BusinessException(ErrorCode.MARKET_DRAW_FAILED,
                        "[" + resp.getCode() + "] " + resp.getInfo());
            }
            return resp.getData();

        } catch (ResourceAccessException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("timeout") || msg.contains("timed out")) {
                throw new BusinessException(ErrorCode.MARKET_SERVICE_TIMEOUT, "营销服务响应超时", e);
            }
            throw new BusinessException(ErrorCode.MARKET_SERVICE_UNAVAILABLE, "营销服务连接失败", e);

        } catch (RestClientResponseException e) {
            throw new BusinessException(ErrorCode.MARKET_RESPONSE_ERROR,
                    "营销服务返回异常: HTTP " + e.getStatusCode());

        } catch (BusinessException e) {
            throw e;

        } catch (Exception e) {
            throw new BusinessException(ErrorCode.MARKET_SERVICE_UNAVAILABLE, "营销服务调用异常", e);
        }
    }

    /**
     * 查询用户活动账户额度
     */
    public UserActivityAccountData queryUserActivityAccount(String userId, String activityId) {
        AccountQueryRequestPayload body = new AccountQueryRequestPayload(userId, activityId);
        BigMarketResponse<UserActivityAccountData> resp = post("query_user_activity_account", body,
                new ParameterizedTypeReference<BigMarketResponse<UserActivityAccountData>>() {});
        return resp.getData();
    }

    /**
     * 查询用户积分（form 参数）
     */
    public BigDecimal queryUserCreditAccount(String userId) {
        BigMarketResponse<BigDecimal> resp = postForm("query_user_credit_account", userId,
                new ParameterizedTypeReference<BigMarketResponse<BigDecimal>>() {});
        return resp.getData();
    }

    /**
     * 签到返利（form 参数）
     */
    public Boolean calendarSignRebate(String userId) {
        BigMarketResponse<Boolean> resp = postForm("calendar_sign_rebate", userId,
                new ParameterizedTypeReference<BigMarketResponse<Boolean>>() {});
        return resp.getData();
    }

    /**
     * 查询签到状态（form 参数）
     */
    public Boolean isCalendarSignRebate(String userId) {
        BigMarketResponse<Boolean> resp = postForm("is_calendar_sign_rebate", userId,
                new ParameterizedTypeReference<BigMarketResponse<Boolean>>() {});
        return resp.getData();
    }

    /**
     * 查询 SKU 商品列表（form 参数）
     */
    public List<SkuProductData> querySkuProductListByActivityId(String activityId) {
        String formBody = "activityId=" + activityId;
        BigMarketResponse<List<SkuProductData>> resp = doPost("query_sku_product_list_by_activity_id", formBody,
                new ParameterizedTypeReference<BigMarketResponse<List<SkuProductData>>>() {},
                "application/x-www-form-urlencoded");
        return resp.getData();
    }

    // ========== 底层 HTTP 方法 ==========

    /**
     * POST JSON body
     */
    private <T, R> BigMarketResponse<R> post(String path, T body, ParameterizedTypeReference<BigMarketResponse<R>> typeRef) {
        return doPost(path, body, typeRef, "application/json");
    }

    /**
     * POST 纯字符串 body（如 credit 查询）
     */
    private <R> BigMarketResponse<R> postRawBody(String path, String body, ParameterizedTypeReference<BigMarketResponse<R>> typeRef) {
        return doPost(path, body, typeRef, "application/json");
    }

    /**
     * POST form 参数（如签到）
     */
    private <R> BigMarketResponse<R> postForm(String path, String userId, ParameterizedTypeReference<BigMarketResponse<R>> typeRef) {
        String formBody = "userId=" + userId;
        return doPost(path, formBody, typeRef, "application/x-www-form-urlencoded");
    }

    private <R> BigMarketResponse<R> doPost(String path, Object body,
                                            ParameterizedTypeReference<BigMarketResponse<R>> typeRef,
                                            String contentType) {
        try {
            BigMarketResponse<R> resp = marketRestClient.post()
                    .uri(PREFIX + path)
                    .header("Content-Type", contentType)
                    .body(body)
                    .retrieve()
                    .body(typeRef);

            if (resp == null) {
                throw new BusinessException(ErrorCode.MARKET_RESPONSE_ERROR, "营销服务返回空响应");
            }
            if (!SUCCESS_CODE.equals(resp.getCode())) {
                String info = resp.getInfo() != null ? resp.getInfo() : "未知错误";
                log.warn("营销服务返回非成功码: path={}, code={}, info={}", path, resp.getCode(), info);
                throw mapError(resp.getCode(), info);
            }
            return resp;

        } catch (ResourceAccessException e) {
            // 超时或连接失败
            String msg = e.getMessage() != null ? e.getMessage() : "";
            if (msg.contains("timeout") || msg.contains("timed out") || msg.contains("Read timed out")) {
                log.warn("营销服务响应超时: path={}", path, e);
                throw new BusinessException(ErrorCode.MARKET_SERVICE_TIMEOUT, "营销服务响应超时: " + path, e);
            }
            log.warn("营销服务连接失败: path={}", path, e);
            throw new BusinessException(ErrorCode.MARKET_SERVICE_UNAVAILABLE, "营销服务连接失败: " + path, e);

        } catch (RestClientResponseException e) {
            // 非 2xx 响应
            log.warn("营销服务返回异常状态码: path={}, status={}", path, e.getStatusCode(), e);
            throw new BusinessException(ErrorCode.MARKET_RESPONSE_ERROR,
                    "营销服务返回异常: HTTP " + e.getStatusCode());

        } catch (BusinessException e) {
            throw e;

        } catch (Exception e) {
            log.warn("营销服务调用异常: path={}", path, e);
            throw new BusinessException(ErrorCode.MARKET_SERVICE_UNAVAILABLE, "营销服务调用异常: " + path, e);
        }
    }

    /**
     * 将 big-market 的业务错误码映射为内部 ErrorCode
     */
    private BusinessException mapError(String code, String info) {
        // big-market 常见错误码映射
        return switch (code) {
            case "0001" -> new BusinessException(ErrorCode.MARKET_ACTIVITY_NOT_ARMORY, info);
            default -> new BusinessException(ErrorCode.MARKET_DRAW_FAILED,
                    "[" + code + "] " + info);
        };
    }
}
