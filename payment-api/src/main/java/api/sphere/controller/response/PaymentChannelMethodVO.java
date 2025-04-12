package api.sphere.controller.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentChannelMethodVO {


    /**
     * id
     */
    private Long id;

    /**
     * channel
     * 对接的渠道编码
     */
    private String channelCode;

    /**
     * channel
     * 对接的渠道编码
     */
    private String channelName;

    /**
     * payment
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * detailed name
     * 名称
     */
    private String paymentName;

    /**
     * 交易方向 见枚举
     */
    private Integer paymentDirection;

    /**
     * attribute
     * 属性
     */
    private String paymentAttribute;

    /**
     * 描述
     */
    private String description;

    /**
     * 结算周期
     */
    private String settleType;

    /**
     * 结算时间
     */
    private String settleTime;

    /**
     * Fees charged for each transaction
     * 单笔费用
     */
    private BigDecimal singleFee = BigDecimal.ZERO;

    /**
     * Fees charged for each transaction rate
     * 单笔费率
     */
    private BigDecimal singleRate = BigDecimal.ZERO;

    /**
     * Lower amount limit, default:0
     * 单笔金额下限
     */
    private BigDecimal amountLimitMin = BigDecimal.ZERO;

    /**
     * Limit amount ceiling
     * 单笔金额上限
     */
    private BigDecimal amountLimitMax = new BigDecimal("1000000000");

    /**
     * Lower times limit
     * 次数下限
     */
    private Integer timesLimitMin = 0;

    /**
     * Limit times ceiling
     * 次数上限
     */
    private Integer timesLimitMax = 10000;

    /**
     * 成功率
     */
    private Double successRate;

    /**
     * 0/1 status
     * 状态
     */
    private boolean status;

    /**
     * remark
     */
    private String remark;
}
