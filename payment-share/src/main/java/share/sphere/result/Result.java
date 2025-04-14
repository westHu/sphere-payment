package share.sphere.result;

import share.sphere.exception.ExceptionCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 返回实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Result<T> extends BaseResult {

    private T data;

    public static <T> Result<T> ok(T data) {
        Result<T> result = new Result<>();
        result.setCode(ExceptionCode.SUCCESS.getCode());
        result.setMessage(ExceptionCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> error(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

}

