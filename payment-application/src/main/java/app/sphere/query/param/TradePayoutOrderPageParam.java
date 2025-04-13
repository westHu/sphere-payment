package app.sphere.query.param;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TradePayoutOrderPageParam extends PageParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易单号列表
     */
    private List<String> tradeNoList;

    /**
     * 商户订单号
     */
    private String orderNo;

    /**
     * 商户订单号列表
     */
    private List<String> orderNoList;

    /**
     * 最小金额
     */
    private BigDecimal amountMin;

    /**
     * 最大金额
     */
    private BigDecimal amountMax;

    /**
     * 出款账户
     */
    private String cashAccount;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

    /**
     * 回调状态
     */
    private Integer callBackStatus;

    /**
     * 交易开始时间
     */
    private String tradeStartTime;

    /**
     * 交易结束时间
     */
    private String tradeEndTime;

    /**
     * 支付完成开始时间
     */
    private String paymentFinishStartTime;

    /**
     * 支付完成结束时间
     */
    private String paymentFinishEndTime;
}
