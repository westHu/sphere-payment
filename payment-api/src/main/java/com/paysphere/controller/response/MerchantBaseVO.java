package com.paysphere.controller.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantBaseVO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户号
     */
    private String merchantCode; //ok

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户性质：个人、企业、机构
     */
    private Integer merchantType;

    /**
     * 商户等级
     */
    private Integer merchantLevel;

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 代理商名称
     */
    private String agentName;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 区域
     */
    private Integer area;

    /**
     * 扩展
     */
    private String attribute;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
