package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户代付配置
 * 记录商户的代付基础配置信息，包括审核设置、扣款方式等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "merchant_payout_config")
public class MerchantPayoutConfig extends BaseMerchantEntity {

    // ============== 业务配置 ==============
    /**
     * 代付人工审核开关
     * true: 开启人工审核
     * false: 关闭人工审核
     */
    private boolean review;

    /**
     * 扣款方式
     * 0: 内扣（从交易金额中扣除手续费）
     * 1: 外扣（额外收取手续费）
     */
    private Integer deductionType;

    // ============== 扩展属性 ==============
    /**
     * 扩展信息
     * JSON格式，用于存储其他扩展配置
     */
    private String attribute;
}
