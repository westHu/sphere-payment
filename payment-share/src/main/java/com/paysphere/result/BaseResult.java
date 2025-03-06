package com.paysphere.result;

import cn.hutool.json.JSONUtil;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.utils.ValidationUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 基础返回实体
 */
@Data
@Slf4j
public class BaseResult {

    private Integer code;
    private String message;
    // private String traceId;

    public static <T> T parse(Result<T> result) {
        return parse(result, true, true);
    }

    public static <T> T parse(Result<T> result, boolean info) {
        return parse(result, true, info);
    }

    public static <T> T parse(Result<T> result, boolean validate, boolean info) {
        if (info) {
            log.info("post result={}", JSONUtil.toJsonStr(result));
        }

        if (Objects.isNull(result)) {
            throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "post result is null");
        }

        if (!ExceptionCode.SUCCESS.getCode().equals(result.getCode())) {
            throw new PaymentException(result.getCode(), result.getMessage());
        }

        if (Objects.isNull(result.getData())) {
            log.error("post error: data is null");
            return null;
        }

        T t = result.getData();
        if (validate) {
            String errorMsg = ValidationUtil.getErrorMsg(t);
            if (StringUtils.isNotBlank(errorMsg)) {
                throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, errorMsg);
            }
        }

        return t;
    }

}


