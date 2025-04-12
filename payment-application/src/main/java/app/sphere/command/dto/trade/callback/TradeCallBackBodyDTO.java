package app.sphere.command.dto.trade.callback;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeCallBackBodyDTO {

    /**
     * 交易单号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 商户系统的订单号
     */
    private String orderNo;

    /**
     * 商户号
     */
    @NotBlank(message = "tradeNo is required")
    private String merchantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "merchantName is required")
    private String merchantName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 状态
     */
    @NotBlank(message = "status is required")
    private String status;

    /**
     * 支付完成时间
     */
    private String transactionTime;

    /**
     * 订单金额
     */
    private TradeCallBackMoneyDTO money;

    /**
     * 手续费金额
     */
    private TradeCallBackMoneyDTO fee;

}
