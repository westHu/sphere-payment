package com.paysphere.utils;

import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;
import java.util.stream.Collectors;


/**
 * 校验工具类
 */
@Slf4j
public class ValidationUtil {


    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    private ValidationUtil() {
        throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Utility classes should not have public " +
                "constructors");
    }

    /**
     * Validates a bean instance and returns a set of constraint violations.
     *
     * @param object the object to validate
     * @param <T>    the type of the object
     * @return a set of constraint violations; if empty, the object is valid
     */
    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }

    /**
     * Validates a bean instance and returns a formatted string of constraint violations.
     * If the object is valid, returns an empty string.
     *
     * @param object the object to validate
     * @param <T>    the type of the object
     * @return a formatted string of constraint violations, or an empty string if the object is valid
     */
    public static <T> String getErrorMsg(T object) {
        Set<ConstraintViolation<T>> violations = validate(object);
        if (CollectionUtils.isEmpty(violations)) {
            return null;
        }
        return violations.stream()
                .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                .collect(Collectors.joining(", "));
    }

}