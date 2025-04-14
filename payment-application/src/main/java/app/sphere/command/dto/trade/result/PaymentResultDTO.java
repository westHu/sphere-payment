package app.sphere.command.dto.trade.result;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentResultDTO {

    /**
     * 渠道错误信息
     */
    private String errorMsg;

    /**
     * 单笔费用
     */
    private BigDecimal singleFee;

    /**
     * 单笔费率
     */
    private BigDecimal singleRate;

    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    // == 收款
    /**
     * QR
     */
    private String qrString;

    /**
     * 支付链接
     */
    private String paymentUrl;


    // == 出款

    /**
     * 出款账号
     */
    private String bankAccount;

    /**
     * 银行账户名
     */
    private String bankAccountName;


}
