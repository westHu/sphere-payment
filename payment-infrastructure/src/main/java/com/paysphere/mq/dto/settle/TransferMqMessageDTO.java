package com.paysphere.mq.dto.settle;

import lombok.Data;

@Data
public class TransferMqMessageDTO {

    /**
     * 业务单号
     */
    private String businessNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 审核状态
     */
    private Boolean reviewStatus;

    /**
     * 转入商户ID
     */
    private String transferToMerchantId;

    /**
     * 转入商户名称
     */
    private String transferToMerchantName;

    /**
     * 转入账户
     */
    private String transferToAccount;


}
