package interview.guide.modules.account.dto;

import lombok.Data;

/**
 * 额度调整回调请求 DTO（big-market → 面试平台）
 */
@Data
public class AdjustQuotaRequestDTO {

    /** 应用ID（用于鉴权） */
    private String appId;

    /** 应用Token（用于鉴权） */
    private String appToken;

    /** 用户ID（对应面试平台的用户ID） */
    private String openid;

    /** 增加额度数量 */
    private Integer increaseQuota;

    /** 额度类型（interview_count / document_analyze） */
    private String quotaType;
}
