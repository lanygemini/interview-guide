package interview.guide.modules.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动信息 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityInfoDTO {
    private String id;
    private String name;
}
