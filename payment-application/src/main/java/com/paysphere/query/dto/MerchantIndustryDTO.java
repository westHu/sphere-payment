package com.paysphere.query.dto;

import lombok.Data;

@Data
public class MerchantIndustryDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 父节点
     */
    private Long parentId;

    /**
     * 行业名称
     */
    private String name;


}
