package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantCalculateCashFeeCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 交易金额
     */
    private BigDecimal amount;

}
