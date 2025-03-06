package com.paysphere.controller.request;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantListReq {

    /**
     * 商户ID
     */
    @Length(max = 16)
    private String merchantId;

    /**
     * 商户名称
     */
    @Length(max = 64)
    private String merchantName;

    /**
     * 商户性质：个人、企业
     */
    private Integer merchantType;

    /**
     * 商户状态
     */
    private Integer status;

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 代理商名称
     */
    private String agentName;

    /**
     * 创建开始时间
     */
    private String createStartTime;

    /**
     * 创建开始时间
     */
    private String createEndTime;

    /**
     * 地区
     */
    private Integer area;
}
