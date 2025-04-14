package share.sphere.result;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.util.Objects;

@Slf4j
@Data
public class ColaSingleResult<T> {

    private boolean success;

    private String errCode;

    private String errMessage;

    private T data;


    public static <T> T parse(ColaSingleResult<T> result) {
        log.info("colaSingleResult={}", JSONUtil.toJsonStr(result));

        if (Objects.isNull(result)) {
            log.error("Post error: colaSingleResult is null");
            throw new PaymentException(ExceptionCode.SYSTEM_ERROR, "post result is null");
        }

        if (!result.success) {
            log.error("Post error colaSingleResult:{}", result.getErrMessage());
            throw new PaymentException(500, result.getErrMessage());
        }

        T t = result.getData();

        if (Objects.isNull(t)) {
            return null;
        }

        return t;
    }

}
