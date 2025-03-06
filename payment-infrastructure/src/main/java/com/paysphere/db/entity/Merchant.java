package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * 商户信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant")
public class Merchant extends BaseEntity {

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
     * 品牌名称(扩展)
     */
    private String brandName;

    /**
     * api 对接模式
     * 1. api
     * 2. 收银台
     * 3 都有
     */
    private Integer apiMode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 地区
     */
    private Integer area;

    /**
     * 数字货币
     */
    private Integer digital;

    /**
     * 扩展
     */
    private String attribute;

}
