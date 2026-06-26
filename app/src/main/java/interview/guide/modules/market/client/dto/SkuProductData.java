package interview.guide.modules.market.client.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * SKU 商品
 */
@Data
public class SkuProductData {
    private String sku;
    private String productName;
    private Integer originalPrice;
    private Integer deductionPrice;
    private BigDecimal credit;
}
