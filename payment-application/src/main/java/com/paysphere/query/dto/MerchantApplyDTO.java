package com.paysphere.query.dto;

import lombok.Data;

@Data
public class MerchantApplyDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道类型 银行、钱包 bank、wallet
     */
    private String channelType;

    /**
     * 申请内容
     */
    private String applyContent;

    /**
     * 申请状态
     */
    private Integer applyStatus;

    /**
     * 申请说明
     */
    private String examineComment;

}
