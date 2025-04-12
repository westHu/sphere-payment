package app.sphere.command.dto;

import lombok.Data;

@Data
public class TradePayChannelDTO {

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * QR
     */
    private String qrString;

    /**
     * 支付链接
     */
    private String paymentUrl;

}
