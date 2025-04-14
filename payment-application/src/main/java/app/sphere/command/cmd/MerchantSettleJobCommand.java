package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantSettleJobCommand {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 交易时间
     */
    private String beginTradeDate;

    /**
     * 交易时间
     */
    private String endTradeDate;
}
