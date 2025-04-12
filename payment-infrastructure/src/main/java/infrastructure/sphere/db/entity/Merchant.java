package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 商户实体
 * 记录系统中的商户基本信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant")
public class Merchant extends BaseEntity {

    // ============== 基础信息 ==============
    /**
     * 商户ID
     * 商户的唯一标识符
     */
    private String merchantId;

    /**
     * 商户名称
     * 商户的正式名称
     */
    private String merchantName;

    /**
     * 品牌名称
     * 商户的品牌名称，用于展示
     */
    private String brandName;

    // ============== 业务属性 ==============
    /**
     * 商户性质
     * 1: 个人
     * 2: 企业
     * 3: 机构
     */
    private Integer merchantType;

    /**
     * 商户等级
     * 用于区分商户的等级，如：普通商户、VIP商户等
     */
    private Integer merchantLevel;

    /**
     * API对接模式
     * 1: API模式
     * 2: 收银台模式
     * 3: API+收银台模式
     */
    private Integer apiMode;

    /**
     * 支持的币种
     * 商户支持的交易币种列表，如：CNY, USD, EUR等
     */
    private List<String> currencyList;

    /**
     * 商户标签
     */
    private List<String> tags;

    // ============== 状态控制 ==============
    /**
     * 商户状态
     * 0: 禁用
     * 1: 启用
     */
    private Integer status;

    // ============== 扩展属性 ==============
    /**
     * 扩展属性
     * 用于存储商户的额外配置信息，JSON格式
     */
    private String attribute;
}
