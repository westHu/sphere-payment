package com.paysphere.command.cmd;


import com.paysphere.command.dto.MerchantProfitDTO;
import com.paysphere.enums.AccountOptTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SettleAccountUpdateCommand {

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
    private AccountOptTypeEnum accountOptType;

    /**
     * 币种
     */
    private String currency;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 商户手续费
     */
    private BigDecimal merchantFee;

    /**
     * 商户分润
     */
    private BigDecimal merchantProfit;

    /**
     * 商户分润明细
     */
    private List<MerchantProfitDTO> merchantProfitList;

    /**
     * 渠道成本费用
     */
    private BigDecimal channelCost;

    /**
     * 商户到账金额
     */
    private BigDecimal accountAmount;

    /**
     * 平台盈利
     */
    private BigDecimal platformProfit;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 扣费方式
     */
    private Integer deductionType;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 外部订单号
     */
    private String outerNo;

}
