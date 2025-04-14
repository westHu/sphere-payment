package app.sphere.command.cmd;


import share.sphere.enums.AccountOptTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SettleAccountUpdateRefundCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 操作类型
     */
    private AccountOptTypeEnum accountOptType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 商户分润退款
     */
    private BigDecimal refundMerchantProfit;

    /**
     * 商户到账金额退款
     */
    private BigDecimal refundAccountAmount;

    /**
     * 平台盈利退款
     */
    private BigDecimal refundPlatformProfit;

    /**
     * 扣费方式
     */
    private Integer deductionType;

}
