package com.paysphere.command.dto;


import com.paysphere.query.dto.MerchantIdDTO;
import lombok.Data;


@Data
public class MerchantQrisConfigApiDTO {

    /**
     * 商户信息
     */
    private MerchantIdDTO merchant;

    /**
     * 标题
     */
    private String shopName;

    /**
     * WhatsApp 电话
     */
    private String whatsAppNotification;

    /**
     * SC信息
     */
    private MerchantQrisConfigApiDetailDTO qrisSc;

    /**
     * 附件信息
     */
    private String additionalInfo;

}
