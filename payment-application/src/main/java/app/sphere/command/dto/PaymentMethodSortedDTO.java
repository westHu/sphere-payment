package app.sphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class PaymentMethodSortedDTO {

    /**
     * 支付方式类型
     */
    private Integer paymentType;

    /**
     * 支付方式
     */
    private List<String> paymentMethodList;

}
