package com.paysphere.controller.response;

import lombok.Data;

/**
 * 商户QRIS配置
 */
@Data
public class MerchantQrisConfigVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 标题
     */
    private String qrisTitle;

    /**
     * 来自
     */
    private String qrisFrom;

    /**
     * 类型
     */
    private String qrisType;

    /**
     * 静态码ID
     */
    private String qrisId;

    /**
     * 静态码内容 json
     */
    private String qrisContent;

    /**
     * 扩展信息
     */
    private String attribute;

}
