package infrastructure.sphere.remote.channel;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 回调参数体
 */

@Data
public class BaseCallBackDTO<P> {

    /**
     * 渠道流水号
     */
    private String channelOrderNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 支付单号
     */
    private String paymentNo;

    /**
     * 回调的订单状态
     */
    private Integer channelStatus;

    /**
     * 渠道成功
     */
    private BigDecimal channelCost;

    /**
     * 回调如果失败 失败原因
     */
    private String channelError;

    /**
     * 订单完成时间
     */
    private LocalDateTime channelTime;

    /**
     * 渠道回调签名或者token
     */
    private String callbackSign;

    /**
     * 接受的数据体
     */
    private P payload;

    /**
     * 是否忽略终态
     */
    private boolean ignoreFinalStatus = false;

    /**
     * 扩展字段 付款人姓名，付款人手机号
     * {}
     */
    private String additionInfo;


}
