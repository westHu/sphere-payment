package com.paysphere.command.dto;

import lombok.Data;


@Data
public class MerchantQrisConfigApiDetailDTO {

    /**
     * 二维码内容
     */
    private String scText;

    /**
     * 二维码地址
     */
    private String scUrl;
}
