package interview.guide.modules.market.client.dto;

import lombok.Data;

/**
 * 用户活动账户额度（与 big-market 返回字段对齐）
 */
@Data
public class UserActivityAccountData {
    private Integer totalCount;
    private Integer totalCountSurplus;
    private Integer dayCount;
    private Integer dayCountSurplus;
    private Integer monthCount;
    private Integer monthCountSurplus;
}
