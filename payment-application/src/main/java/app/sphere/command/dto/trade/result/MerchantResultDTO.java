package app.sphere.command.dto.trade.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantResultDTO {

    /**
     * 渠道错误信息
     */
    private String errorMsg;

    /**
     * 商户收款完成回调地址
     */
    private String finishPaymentUrl;

    /**
     * 商户出款完成回调地址
     */
    private String finishCashUrl;

    /**
     * 商户跳转地址
     */
    private String finishRedirectUrl;

    /**
     * 扣款方式： 0内扣 1外扣
     */
    private Integer deductionType;

    /**
     * 费用
     */
    private BigDecimal singleFee;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 结算配置
     */
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;

}
