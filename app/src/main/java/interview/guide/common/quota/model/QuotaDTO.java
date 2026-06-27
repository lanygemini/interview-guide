package interview.guide.common.quota.model;

import interview.guide.common.quota.QuotaType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户额度 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuotaDTO {
    private String type;
    private String displayName;
    private int remaining;
    private int total;
    private int used;
}
