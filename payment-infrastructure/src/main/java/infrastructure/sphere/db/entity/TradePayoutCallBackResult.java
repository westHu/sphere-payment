package infrastructure.sphere.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代付订单回调结果实体
 * 记录支付渠道对代付订单的回调结果，包括回调状态、回调内容等
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "trade_payout_callback_result")
public class TradePayoutCallBackResult extends BaseEntity {

    // ============== 订单信息 ==============
    /**
     * 代付单号
     * 关联的代付订单号
     */
    private String tradeNo;

    // ============== 回调信息 ==============
    /**
     * 回调时间
     * 支付渠道发起回调的时间戳
     */
    private Long callBackTime;

    /**
     * 回调状态
     * 0: 未处理
     * 1: 处理中
     * 2: 处理成功
     * 3: 处理失败
     */
    private Integer callBackStatus;

    /**
     * 回调结果
     * 支付渠道返回的原始回调内容，JSON格式
     */
    private String callBackResult;

    // ============== 扩展属性 ==============
    /**
     * 扩展字段
     * 用于存储额外的回调信息，JSON格式
     */
    private String attribute;

}
