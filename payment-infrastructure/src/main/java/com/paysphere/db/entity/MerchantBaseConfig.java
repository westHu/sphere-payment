package com.paysphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_base_config")
public class MerchantBaseConfig extends BaseEntity {

    /**
     * 配置类型
     */
    private String configType;

    /**
     * 键
     */
    private String name;

    /**
     * 键
     */
    private String module;

    /**
     * 值
     */
    private String value;

    /**
     * 扩展信息
     */
    private String attribute;

}
