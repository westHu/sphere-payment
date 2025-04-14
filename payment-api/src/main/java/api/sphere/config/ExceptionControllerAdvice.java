package api.sphere.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import share.sphere.result.ErrorResult;
import share.sphere.utils.PlaceholderUtil;

import java.util.Optional;

/**
 * 全局异常处理器
 * 统一处理系统中的各类异常，并返回标准化的错误响应
 * 
 * 处理流程：
 * 1. 捕获系统抛出的各类异常
 * 2. 根据异常类型进行分类处理
 * 3. 记录详细的错误日志
 * 4. 返回统一的错误响应格式
 * 
 * 支持的异常类型：
 * 1. PaymentException: 业务异常，包含业务错误码和错误信息
 * 2. MethodArgumentNotValidException: 参数校验异常，通常由@Valid注解触发
 * 3. IllegalArgumentException: 非法参数异常，通常由业务逻辑校验触发
 * 4. Exception: 其他未预期的异常
 * 
 * 日志记录：
 * 1. 记录异常类型和详细信息
 * 2. 记录异常堆栈信息
 * 3. 记录错误码和错误消息
 */
@RestControllerAdvice
@ControllerAdvice
@Slf4j
public class ExceptionControllerAdvice {

    /**
     * 处理业务异常
     * 处理系统中定义的业务异常，返回对应的错误码和错误信息
     * 
     * @param e 业务异常
     * @return 错误响应
     */
    @ExceptionHandler(PaymentException.class)
    public ErrorResult handlerPaymentException(PaymentException e) {
        log.error("业务异常: code={}, message={}", e.getCode(), e.getMessage(), e);
        return createDataResult(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常
     * 处理由@Valid注解触发的参数校验异常，返回参数错误信息
     * 
     * @param e 参数校验异常
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResult handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMsg = Optional.ofNullable(e)
                .map(MethodArgumentNotValidException::getBindingResult)
                .map(Errors::getFieldError)
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("Method argument not valid exception");
        log.error("参数校验异常: message={}, field={}", 
                errorMsg, 
                Optional.ofNullable(e)
                        .map(MethodArgumentNotValidException::getBindingResult)
                        .map(Errors::getFieldError)
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .orElse("unknown"), 
                e);
        return createDataResult(ExceptionCode.SYSTEM_ERROR, errorMsg);
    }

    /**
     * 处理非法参数异常
     * 处理由业务逻辑校验触发的非法参数异常
     * 
     * @param e 非法参数异常
     * @return 错误响应
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult handlerIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常: message={}", e.getMessage(), e);
        return createDataResult(ExceptionCode.SYSTEM_ERROR, e.getMessage());
    }

    /**
     * 处理其他未预期的异常
     * 处理系统中未明确处理的异常，返回通用错误信息
     * 
     * @param e 未预期的异常
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ErrorResult handlerException(Exception e) {
        log.error("系统异常: message={}", e.getMessage(), e);
        return createDataResult(ExceptionCode.SYSTEM_BUSY, e.getMessage());
    }

    /**
     * 创建错误响应
     * 根据异常码和错误信息创建标准化的错误响应
     * 
     * @param exceptionCode 异常码
     * @param errorMsg 错误信息
     * @return 错误响应
     */
    private ErrorResult createDataResult(ExceptionCode exceptionCode, String errorMsg) {
        ErrorResult result = new ErrorResult();
        result.setCode(exceptionCode.getCode());
        result.setMessage(PlaceholderUtil.resolve(exceptionCode.getMessage(), errorMsg));
        log.debug("创建错误响应: code={}, message={}", result.getCode(), result.getMessage());
        return result;
    }

    /**
     * 创建错误响应
     * 根据错误码和错误信息创建标准化的错误响应
     * 
     * @param code 错误码
     * @param errorMsg 错误信息
     * @return 错误响应
     */
    private ErrorResult createDataResult(Integer code, String errorMsg) {
        ErrorResult result = new ErrorResult();
        result.setCode(code);
        result.setMessage(errorMsg);
        log.debug("创建错误响应: code={}, message={}", result.getCode(), result.getMessage());
        return result;
    }
}
