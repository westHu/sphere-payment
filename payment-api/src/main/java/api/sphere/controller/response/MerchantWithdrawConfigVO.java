package api.sphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 商户支付配置
 */
@Data
public class MerchantWithdrawConfigVO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 提现支付方式
     */
    private String withdrawPaymentMethod;

    /**
     * 提现账户
     */
    private String withdrawAccount;

    /**
     * 提现人工审核开关
     */
    private Boolean withdrawReview;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 提现费用
     */
    private BigDecimal withdrawFee;

    /**
     * 提现费率
     */
    private BigDecimal withdrawRate;

    /**
     * 扩展信息
     */
    private String attribute;


}
