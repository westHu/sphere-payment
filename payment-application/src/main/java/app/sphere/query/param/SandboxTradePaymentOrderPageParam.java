package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SandboxTradePaymentOrderPageParam extends PageParam {

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
