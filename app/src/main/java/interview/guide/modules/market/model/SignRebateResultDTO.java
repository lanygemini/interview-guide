package interview.guide.modules.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 签到返利结果 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SignRebateResultDTO {
    private boolean success;
}
