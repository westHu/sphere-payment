package api.sphere.job.param;

import lombok.Data;

@Data
public class MerchantSettleJobParam {

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
