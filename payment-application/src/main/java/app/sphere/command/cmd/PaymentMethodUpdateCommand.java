package app.sphere.command.cmd;

import lombok.Data;

@Data
public class PaymentMethodUpdateCommand {

    /**
     * ID
     */
    private Long id;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 支付方式类型
     */
    private Integer paymentType;

    /**
     * 支付方式方向
     */
    private Integer paymentDirection;

    /**
     * 支付方式图标
     */
    private String paymentIcon;

}
