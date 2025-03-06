package com.paysphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class IndustryTemplateUpdateCommand {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 模版名 支付方式
     */
    private String templateName;

    /**
     * 交易类型
     */
    private Integer tradeType;

    /**
     * ID数组
     */
    private List<Long> idList;

    /**
     * 手续费
     */
    private BigDecimal singleFee;

    /**
     * 手续费率
     */
    private BigDecimal singleRate;

    /**
     * 单笔最小
     */
    private BigDecimal amountLimitMin;

    /**
     * 单笔最大
     */
    private BigDecimal amountLimitMax;

    /**
     * 结算配置
     */
    private String settleType;

    /**
     * 商户渠道配置状态
     */
    private Boolean status;


}
