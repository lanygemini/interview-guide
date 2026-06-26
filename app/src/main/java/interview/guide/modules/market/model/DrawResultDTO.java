package interview.guide.modules.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖结果 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DrawResultDTO {
    private String awardId;
    private String awardTitle;
    private String awardIndex;
}
