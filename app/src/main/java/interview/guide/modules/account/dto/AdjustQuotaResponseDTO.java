package interview.guide.modules.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 额度调整回调响应 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdjustQuotaResponseDTO {

    /** 返回码 */
    private String code;

    /** 返回信息 */
    private String info;
}
