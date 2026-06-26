package interview.guide.modules.market.client.dto;

import lombok.Data;

/**
 * 奖品列表响应 DTO
 */
@Data
public class RaffleAwardListResponseDTO {
    private Integer awardId;
    private String awardTitle;
    private String awardSubtitle;
    private Integer sort;
    private Integer awardRuleLockCount;
    private Boolean isAwardUnlock;
    private Integer waitUnLockCount;
}
