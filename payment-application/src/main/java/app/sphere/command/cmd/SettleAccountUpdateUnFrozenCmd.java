package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SettleAccountUpdateUnFrozenCmd {

    // ===== 交易信息 =====
    /**
     * 交易单号
     */
    private String tradeNo;

    // ===== 商户信息 =====
    /**
     * 商户信息
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

    // ===== 金额信息 =====
    /**
     * 币种
     */
    private String currency;

    /**
     * 解冻金额
     */
    private BigDecimal amount;

    /**
     * 地区 (与币种相关)
     */
    private String region;
}
