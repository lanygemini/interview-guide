package interview.guide.modules.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 奖品 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AwardDTO {
    private Integer awardId;
    private String awardTitle;
    private String awardSubtitle;
    private Integer sort;
    private Integer awardRuleLockCount;
    private Boolean isAwardUnlock;
    private Integer waitUnLockCount;
}
