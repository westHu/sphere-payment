package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户代付支付配置
 */
@Data
public class MerchantPayoutConfigDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 代付人工审核开关
     */
    private Boolean review;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 扩展信息
     */
    private String attribute;

}
