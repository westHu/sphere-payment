package com.paysphere.query.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IndustryTemplatePaymentDTO {

    /**
     * 模版名称
     */
    private String templateName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 费率
     */
    private BigDecimal singleRate;

    /**
     * 费用
     */
    private BigDecimal singleFee;

    /**
     * 单笔最小
     */
    private BigDecimal amountLimitMin;

    /**
     * 单笔最大
     */
    private BigDecimal amountLimitMax;

    /**
     * 结算类型
     */
    private String settleType;

    /**
     * 状态
     */
    private Boolean status;

    /**
     * 渠道列表
     */
    private List<IndustryTemplateChannelDTO> channelList;

}
