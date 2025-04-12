package api.sphere.controller.response;

import lombok.Data;

@Data
public class TradeCashChannelVO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 出款账号
     */
    private String cashAccount;

    /**
     * 银行账户名
     */
    private String accountName;
}
