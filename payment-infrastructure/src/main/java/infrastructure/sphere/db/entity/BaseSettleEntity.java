package infrastructure.sphere.db.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户基础实体类
 * 所有数据库实体类的父类，提供基础字段
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseSettleEntity extends BaseEntity {

    // ============== 商户信息 ==============
    /**
     * 商户ID
     * 商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 商户的显示名称
     */
    private String merchantName;

    // ============== 账户信息 ==============
    /**
     * 账户号
     * 发生资金变动的账户号
     */
    private String accountNo;
}
