package com.paysphere.command.cmd;

import lombok.Data;

@Data
public class MerchantApplyAccountBackCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 审核结果
     */
    private Boolean examineStatus;

    /**
     * 审核说明
     */
    private String examineComment;
}
