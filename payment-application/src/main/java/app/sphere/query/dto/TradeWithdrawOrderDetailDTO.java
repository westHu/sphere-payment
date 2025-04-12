package app.sphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeWithdrawOrderDetailDTO {

    /**
     * 代付单号
     */
    private String tradeNo;

    /**
     * 交易目的
     */
    private String purpose;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 出款账号
     */
    private String withdrawAccount;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户账户号
     */
    private String accountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 代付金额
     */
    private BigDecimal amount;

    /**
     * 实扣金额
     */
    private BigDecimal actualAmount;

    /**
     * 到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 交易时间
     */
    private LocalDateTime tradeTime;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 结算完成时间
     */
    private Integer settleFinishTime;

    /**
     * 截图证据
     */
    private String proof;

}
