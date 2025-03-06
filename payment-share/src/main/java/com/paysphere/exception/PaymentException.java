package com.paysphere.exception;

import com.paysphere.utils.PlaceholderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统统一根异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentException extends RuntimeException {

    private final int code;
    private final String message;

    public PaymentException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public PaymentException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public PaymentException(ExceptionCode exceptionCode) {
        this(exceptionCode.getCode(), exceptionCode.getMessage());
    }

    public PaymentException(ExceptionCode exceptionCode, Object... param) {
        this(exceptionCode.getCode(), PlaceholderUtil.resolve(exceptionCode.getMessage(), param));
    }
}
