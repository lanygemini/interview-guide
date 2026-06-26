package interview.guide.modules.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动账户额度 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityAccountDTO {
    private Integer totalCount;
    private Integer usedCount;
    private Integer leftCount;
}
