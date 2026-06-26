package interview.guide.modules.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 积分账户 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditAccountDTO {
    private BigDecimal credit;
}
