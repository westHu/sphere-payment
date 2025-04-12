package app.sphere.command.dto.trade.result;

import lombok.Data;

@Data
public class ChannelResultDTO {



    /**
     * 渠道订单号
     */
    private String channelOrderNo;

    /**
     * 渠道结果状态
     */
    private String status;


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
    private String cashAccount;

    /**
     * 银行账户名
     */
    private String accountName;

}
