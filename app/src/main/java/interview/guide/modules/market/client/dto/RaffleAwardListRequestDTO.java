package interview.guide.modules.market.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询奖品列表请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaffleAwardListRequestDTO {
    private String userId;
    private Long activityId;
}
