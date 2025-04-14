package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentChannelMethodUpdateCommand {

    /**
     * id
     */
    private Long id;

    /**
     * 支付方式属性
     */
    private String paymentAttribute;

    /**
     * 单笔手续费
     */
    private BigDecimal singleFee;

    /**
     * 单笔手续费率
     */
    private BigDecimal singleRate;

    /**
     * 限额下限
     */
    private BigDecimal amountLimitMin;

    /**
     * 限额上限
     */
    private BigDecimal amountLimitMax;

}
