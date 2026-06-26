package interview.guide.modules.market.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询用户活动账户额度请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountQueryRequestPayload {
    private String userId;
    private String activityId;
}
