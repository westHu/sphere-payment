package api.sphere.controller.request;

import lombok.Data;

@Data
public class SandboxTradePayOrderPageReq extends PageReq {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户姓名
     */
    private String merchantName;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 商户订单号
     */
    private String orderNo;

}
