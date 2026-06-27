package interview.guide.common.quota;

import interview.guide.common.exception.BusinessException;
import interview.guide.common.exception.ErrorCode;

/**
 * 额度不足异常
 */
public class QuotaExceededException extends BusinessException {

    private final QuotaType quotaType;

    public QuotaExceededException(QuotaType quotaType) {
        super(ErrorCode.QUOTA_EXCEEDED, String.format(ErrorCode.QUOTA_EXCEEDED.getMessage(), quotaType.getDisplayName()));
        this.quotaType = quotaType;
    }

    public QuotaType getQuotaType() {
        return quotaType;
    }
}
