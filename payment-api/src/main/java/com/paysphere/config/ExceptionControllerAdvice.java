package com.paysphere.config;

import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.result.ErrorResult;
import com.paysphere.utils.PlaceholderUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

/**
 * exception 控制
 */
@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    /**
     * 可知业务异常
     */
    @ExceptionHandler(PaymentException.class)
    public ErrorResult handlerPaymentException(PaymentException e) {
        log.error("handlerPaymentException:", e);
        return createDataResult(e.getCode(), e.getMessage());
    }

    /**
     * 非法参数异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handlerMethodArgumentNotValidException:", e);
        String errorMsg = Optional.ofNullable(e)
                .map(MethodArgumentNotValidException::getBindingResult)
                .map(Errors::getFieldError)
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Method argument not valid exception");
        return createDataResult(ExceptionCode.PARAM_IS_INVALID, errorMsg);
    }

    /**
     * 非法数据异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult handlerIllegalArgumentException(IllegalArgumentException e) {
        log.error("handlerIllegalArgumentException:", e);
        return createDataResult(ExceptionCode.PARAM_IS_INVALID, e.getMessage());
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public ErrorResult handlerException(Exception e) {
        log.error("handlerException:", e);
        return createDataResult(ExceptionCode.GENERAL_ERROR, e.getMessage());
    }


    /**
     * create json result
     */
    private ErrorResult createDataResult(ExceptionCode exceptionCode, String errorMsg) {
        ErrorResult result = new ErrorResult();
        result.setCode(exceptionCode.getCode());
        result.setMessage(PlaceholderUtil.resolve(exceptionCode.getMessage(), errorMsg));
        return result;
    }

    /**
     * create json result
     */
    private ErrorResult createDataResult(Integer code, String errorMsg) {
        ErrorResult result = new ErrorResult();
        result.setCode(code);
        result.setMessage(errorMsg);
        return result;
    }

}
