package app.sphere.command.dto.trade.result;

import lombok.Data;

@Data
public class TradeResultDTO {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误消息
     */
    private String error;

    /**
     * 执行人
     */
    private String applyOperator;

    /**
     * 交易审核结果
     */
    private ReviewResultDTO reviewResult;

    /**
     * 交易商户结果
     */
    private MerchantResultDTO merchantResult;

    /**
     * 交易支付方式结果
     */
    private PaymentResultDTO paymentResult;

}
