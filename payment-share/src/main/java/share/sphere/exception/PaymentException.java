package share.sphere.exception;

import share.sphere.utils.PlaceholderUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统统一根异常
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentException extends RuntimeException {

    private final Integer code;
    private final String message;

    public PaymentException(String message) {
        super(message);
        this.code = 500;
        this.message = message;
    }

    public PaymentException(Integer code, String message) {
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
