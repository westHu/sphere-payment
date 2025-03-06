package com.paysphere.query.dto;

import com.paysphere.enums.MerchantStatusEnum;
import lombok.Data;

@Data
public class MerchantBaseDTO {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户号
     */
    private String merchantCode;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 商户性质： 个人、企业、机构
     */
    private Integer merchantType;

    /**
     * 等级
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
     * 状态
     *
     * @see MerchantStatusEnum
     */
    private Integer status;

}
