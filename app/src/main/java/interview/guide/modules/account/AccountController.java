package interview.guide.modules.account;

import interview.guide.common.exception.BusinessException;
import interview.guide.common.exception.ErrorCode;
import interview.guide.common.quota.QuotaType;
import interview.guide.common.quota.service.QuotaService;
import interview.guide.common.result.Result;
import interview.guide.modules.account.dto.AdjustQuotaRequestDTO;
import interview.guide.modules.account.dto.AdjustQuotaResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 账户额度调整控制器
 * <p>
 * 接收 big-market 抽奖回调，为用户增加指定类型的额度。
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final QuotaService quotaService;

    @Value("${app.quota.callback.app-id:big-market}")
    private String expectedAppId;

    @Value("${app.quota.callback.app-token:}")
    private String expectedAppToken;

    /**
     * 接收 big-market 发奖回调，增加用户额度
     * <p>
     * 请求体示例：
     * <pre>
     * {
     *   "appId": "big-market",
     *   "appToken": "6ec604541f8b1ce4a",
     *   "openid": "2",
     *   "increaseQuota": 5,
     *   "quotaType": "interview_count"
     * }
     * </pre>
     */
    @PostMapping("/adjust_quota")
    public Result<AdjustQuotaResponseDTO> adjustQuota(@RequestBody AdjustQuotaRequestDTO request) {
        log.info("收到额度调整回调: openid={}, quotaType={}, increaseQuota={}",
                request.getOpenid(), request.getQuotaType(), request.getIncreaseQuota());

        // 1. 鉴权
        if (!expectedAppId.equals(request.getAppId()) || !expectedAppToken.equals(request.getAppToken())) {
            log.warn("回调鉴权失败: appId={}", request.getAppId());
            throw new BusinessException(ErrorCode.QUOTA_CALLBACK_INVALID);
        }

        // 2. 参数校验
        if (request.getOpenid() == null || request.getIncreaseQuota() == null || request.getIncreaseQuota() <= 0) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "参数无效: openid或increaseQuota为空");
        }

        // 3. 解析额度类型
        QuotaType quotaType;
        try {
            quotaType = QuotaType.fromCode(request.getQuotaType());
        } catch (IllegalArgumentException e) {
            log.warn("未知额度类型: {}", request.getQuotaType());
            throw new BusinessException(ErrorCode.BAD_REQUEST, "未知额度类型: " + request.getQuotaType());
        }

        // 4. 增加额度
        Long userId = Long.valueOf(request.getOpenid());
        quotaService.addQuota(userId, quotaType, request.getIncreaseQuota());

        // 5. 返回成功
        AdjustQuotaResponseDTO response = AdjustQuotaResponseDTO.builder()
                .code("0000")
                .info("success")
                .build();
        return Result.success(response);
    }
}
