package com.paysphere.controller.response;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商户账户记录
 */
@Data
public class SettleAccountRecordVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 记录编号
     */
    private String accountRecordNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 操作类型
     */
    private String accountOptType;

    /**
     * 操作名称
     */
    private String accountOptName;

    /**
     * 币种
     */
    private String currency;

    /**
     * 商户变动金额
     */
    private BigDecimal merchantAmount;

    /**
     * 平台变动金额
     */
    private BigDecimal platformAmount;

    /**
     * 关联交易单号
     */
    private String tradeNo;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 记录时间
     */
    private LocalDateTime recordTime;

    /**
     * 扩展
     */
    private String attribute;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;


}
