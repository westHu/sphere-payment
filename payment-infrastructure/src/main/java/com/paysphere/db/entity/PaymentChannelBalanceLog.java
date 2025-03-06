package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * <p>
 * 渠道余额统计表
 * </p>
 *
 * @author ${author}
 * @since 2023-07-06
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("payment_channel_balance_log")
public class PaymentChannelBalanceLog extends BaseEntity {

    /**
     * 币种
     */
    private String currency;

    /**
     * 余额
     */
    private BigDecimal balance;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance;

    /**
     * 处理中金额
     */
    private BigDecimal pendingBalance;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 接口返回数据
     */
    private String orgData;

    /**
     * 备注
     */
    private String attribute;

}
