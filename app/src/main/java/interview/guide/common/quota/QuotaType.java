package interview.guide.common.quota;

/**
 * 额度类型枚举
 */
public enum QuotaType {

    INTERVIEW_COUNT("interview_count", "AI面试次数"),
    DOCUMENT_ANALYZE("document_analyze", "文档分析次数"),
    ;

    private final String code;
    private final String displayName;

    QuotaType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static QuotaType fromCode(String code) {
        for (QuotaType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown quota type: " + code);
    }
}
