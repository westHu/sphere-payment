package com.paysphere.controller.request;

import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class TradeCashierPaymentReq {

    /**
     * 交易单号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 手机号码 有条件地判断 OVO则需要手机号码
     */
    public String getPhone() {
        if (StringUtils.contains(paymentMethod, "OVO") && StringUtils.isBlank(phone)) {
            throw new PaymentException(ExceptionCode.PARAM_IS_REQUIRED, "OVO phone");
        }
        return phone;
    }
}
