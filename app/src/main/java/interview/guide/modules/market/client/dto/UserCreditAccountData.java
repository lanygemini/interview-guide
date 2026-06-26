package interview.guide.modules.market.client.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 用户积分（query_user_credit_account 返回的 data 为 BigDecimal）
 */
@Data
public class UserCreditAccountData {
    private String userId;
    private BigDecimal credit;
}
