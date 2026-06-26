package interview.guide.modules.market.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DrawRequestPayload {
    private String userId;
    private String activityId;
}
