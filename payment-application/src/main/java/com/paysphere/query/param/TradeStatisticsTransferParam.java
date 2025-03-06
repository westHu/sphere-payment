package com.paysphere.query.param;

import lombok.Data;

import java.util.List;

@Data
public class TradeStatisticsTransferParam extends PageParam {

    /**
     * 交易开始日期
     */
    private String tradeStartDate;

    /**
     * 交易结束日期
     */
    private String tradeEndDate;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户类型
     */
    private List<Integer> accountTypeList;

    /**
     * 转账方向
     */
    private Integer transferDirection;

}
