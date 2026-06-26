package interview.guide.modules.market.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * SKU 商品 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkuProductDTO {
    private String sku;
    private String productName;
    private Integer originalPrice;
    private Integer deductionPrice;
    private BigDecimal credit;
}
