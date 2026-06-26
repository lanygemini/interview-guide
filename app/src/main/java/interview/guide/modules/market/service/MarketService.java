package interview.guide.modules.market.service;

import interview.guide.modules.market.client.BigMarketClient;
import interview.guide.modules.market.client.dto.DrawAwardData;
import interview.guide.modules.market.client.dto.RaffleAwardListResponseDTO;
import interview.guide.modules.market.client.dto.SkuProductData;
import interview.guide.modules.market.client.dto.UserActivityAccountData;
import interview.guide.modules.market.config.MarketProperties;
import interview.guide.modules.market.model.ActivityAccountDTO;
import interview.guide.modules.market.model.ActivityInfoDTO;
import interview.guide.modules.market.model.AwardDTO;
import interview.guide.modules.market.model.CreditAccountDTO;
import interview.guide.modules.market.model.DrawResultDTO;
import interview.guide.modules.market.model.SignRebateResultDTO;
import interview.guide.modules.market.model.SkuProductDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 营销服务
 * <p>
 * 不加 @Transactional（外部调用不进事务）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MarketService {

    private final BigMarketClient bigMarketClient;
    private final MarketProperties marketProperties;

    /**
     * 获取可选活动列表
     */
    public List<ActivityInfoDTO> getActivities() {
        return marketProperties.getActivities().stream()
                .map(a -> ActivityInfoDTO.builder()
                        .id(a.getId())
                        .name(a.getName())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 抽奖
     *
     * @param userId     用户ID
     * @param activityId 活动ID（null 则使用默认值）
     */
    public DrawResultDTO draw(String userId, String activityId) {
        String actId = activityId != null ? activityId : marketProperties.getActivityId();
        DrawAwardData award = bigMarketClient.draw(userId, actId);
        return DrawResultDTO.builder()
                .awardId(award.getAwardId())
                .awardTitle(award.getAwardTitle())
                .awardIndex(award.getAwardIndex())
                .build();
    }

    /**
     * 查询活动账户额度
     */
    public ActivityAccountDTO getAccount(String userId, String activityId) {
        String actId = activityId != null ? activityId : marketProperties.getActivityId();
        UserActivityAccountData account = bigMarketClient.queryUserActivityAccount(userId, actId);
        Integer total = account.getTotalCount() != null ? account.getTotalCount() : 0;
        Integer surplus = account.getTotalCountSurplus() != null ? account.getTotalCountSurplus() : 0;
        return ActivityAccountDTO.builder()
                .totalCount(total)
                .usedCount(total - surplus)
                .leftCount(surplus)
                .build();
    }

    /**
     * 查询积分
     */
    public CreditAccountDTO getCredit(String userId) {
        BigDecimal credit = bigMarketClient.queryUserCreditAccount(userId);
        return CreditAccountDTO.builder()
                .credit(credit)
                .build();
    }

    /**
     * 签到返利
     */
    public SignRebateResultDTO sign(String userId) {
        Boolean result = bigMarketClient.calendarSignRebate(userId);
        return SignRebateResultDTO.builder()
                .success(result != null && result)
                .build();
    }

    /**
     * 查询签到状态
     */
    public SignRebateResultDTO getSignStatus(String userId) {
        Boolean result = bigMarketClient.isCalendarSignRebate(userId);
        return SignRebateResultDTO.builder()
                .success(result != null && result)
                .build();
    }

    /**
     * 查询奖品列表
     */
    public List<AwardDTO> getAwardList(String userId, String activityId) {
        String actId = activityId != null ? activityId : marketProperties.getActivityId();
        List<RaffleAwardListResponseDTO> awards = bigMarketClient.queryRaffleAwardList(
                userId, Long.valueOf(actId));
        return awards.stream()
                .map(a -> AwardDTO.builder()
                        .awardId(a.getAwardId())
                        .awardTitle(a.getAwardTitle())
                        .awardSubtitle(a.getAwardSubtitle())
                        .sort(a.getSort())
                        .awardRuleLockCount(a.getAwardRuleLockCount())
                        .isAwardUnlock(a.getIsAwardUnlock())
                        .waitUnLockCount(a.getWaitUnLockCount())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 查询 SKU 商品列表
     */
    public List<SkuProductDTO> getSkus(String activityId) {
        String actId = activityId != null ? activityId : marketProperties.getActivityId();
        List<SkuProductData> products = bigMarketClient.querySkuProductListByActivityId(actId);
        return products.stream()
                .map(p -> SkuProductDTO.builder()
                        .sku(p.getSku())
                        .productName(p.getProductName())
                        .originalPrice(p.getOriginalPrice())
                        .deductionPrice(p.getDeductionPrice())
                        .credit(p.getCredit())
                        .build())
                .collect(Collectors.toList());
    }
}
