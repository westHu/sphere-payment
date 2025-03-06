package com.paysphere.command.dto;

import lombok.Data;

@Data
public class ExamineCommentDTO {

    /**
     * 子商户ID
     */
    private String subMerchantId;


    /**
     * shop名称, 譬如NOBU, 在申请子商户号的同时也会给到静态码
     */
    private String shopName;

    /**
     * 静态码, 譬如NOBU, 在申请子商户号的同时也会给到静态码
     */
    private String qrCode;

    /**
     * WhatsApp 电话
     */
    private String whatsAppNotification;

}
