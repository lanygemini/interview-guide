package interview.guide.modules.market.client.dto;

import lombok.Data;

/**
 * 大营销平台统一响应
 */
@Data
public class BigMarketResponse<T> {

    private String code;
    private String info;
    private T data;

    public boolean isSuccess() {
        return "0000".equals(code);
    }
}
