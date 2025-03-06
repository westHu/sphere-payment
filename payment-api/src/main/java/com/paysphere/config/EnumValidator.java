package com.paysphere.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 校验入参是否为指定enum的值的实现方法
 */
@Slf4j
public class EnumValidator implements ConstraintValidator<EnumValid, Object> {

    String transferMethod;
    Class<?>[] cls; // 枚举类

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        cls = constraintAnnotation.target();
        transferMethod = constraintAnnotation.transferMethod();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value != null && value.toString().length() > 0 && cls.length > 0) {
            for (Class<?> cl : cls) {
                try {
                    if (cl.isEnum()) {
                        // 枚举类验证
                        Object[] objs = cl.getEnumConstants();
                        Method method = cl.getMethod("name");
                        for (Object obj : objs) {
                            Object code = method.invoke(obj, null);
                            if (value.equals(code.toString())) {
                                return true;
                            }
                        }
                        Method codeMethod = cl.getMethod(transferMethod);
                        for (Object obj : objs) {
                            Object code = codeMethod.invoke(obj, null);
                            if (value.toString().equals(code.toString())) {
                                return true;
                            }
                        }
                    }
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    log.error("enum validator is valid exception:", e);
                }
            }
        } else {
            return true;
        }
        return false;
    }

}
