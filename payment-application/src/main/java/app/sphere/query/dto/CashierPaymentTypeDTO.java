package app.sphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class CashierPaymentTypeDTO {

    /**
     * 支付方式类型：信用卡、VA、QR等
     */
    private Integer paymentType;

    /**
     * 支付方式列表
     */
    private List<CashierPaymentMethodDTO> paymentMethodList;

    /**
     * 排序
     */
    private int sort;

}
