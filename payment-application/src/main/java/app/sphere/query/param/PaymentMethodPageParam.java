package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentMethodPageParam extends PageParam {

    /**
     * 支付类型
     */
    private Integer paymentType;

    /**
     * 支付方向
     */
    private Integer paymentDirection;

    /**
     * 支付方式编码
     */
    private String paymentMethod;

    /**
     * 状态
     */
    private Boolean status;


}
