package api.sphere.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 枚举值校验器
 * 实现枚举值的校验逻辑，支持多个枚举类的校验
 * 
 * 校验流程：
 * 1. 初始化：获取目标枚举类和转换方法
 * 2. 校验：
 *    - 如果值为null或空字符串，直接返回true
 *    - 遍历所有目标枚举类
 *    - 对每个枚举类，先检查name()方法
 *    - 如果name()方法不匹配，再检查指定的transferMethod()方法
 *    - 只要有一个枚举类匹配，就返回true
 * 
 * 日志记录：
 * 1. 初始化时记录配置信息
 * 2. 校验时记录详细的校验过程
 * 3. 异常时记录完整的异常堆栈
 */
@Slf4j
public class EnumValidator implements ConstraintValidator<EnumValid, Object> {

    private String transferMethod;
    private Class<?>[] cls; // 枚举类

    @Override
    public void initialize(EnumValid constraintAnnotation) {
        cls = constraintAnnotation.target();
        transferMethod = constraintAnnotation.transferMethod();
        log.debug("初始化枚举校验器: 目标枚举类={}, 转换方法={}", 
                Arrays.toString(cls), transferMethod);
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // 如果值为null或空字符串，直接返回true
        if (value == null || value.toString().length() == 0) {
            log.debug("字段值为空，校验通过");
            return true;
        }

        // 如果没有指定枚举类，返回false
        if (cls == null || cls.length == 0) {
            log.warn("未指定目标枚举类，校验不通过");
            return false;
        }

        // 遍历所有目标枚举类
        for (Class<?> cl : cls) {
            try {
                if (cl.isEnum()) {
                    log.debug("开始校验枚举类: {}", cl.getName());
                    
                    // 获取枚举常量
                    Object[] objs = cl.getEnumConstants();
                    if (objs == null || objs.length == 0) {
                        log.warn("枚举类 {} 没有枚举值", cl.getName());
                        continue;
                    }

                    // 先检查name()方法
                    Method nameMethod = cl.getMethod("name");
                    for (Object obj : objs) {
                        Object code = nameMethod.invoke(obj, new Object[0]);
                        if (value.equals(code.toString())) {
                            log.debug("字段值 {} 匹配枚举类 {} 的name()方法，校验通过", 
                                    value, cl.getName());
                            return true;
                        }
                    }

                    // 再检查指定的transferMethod()方法
                    try {
                        Method codeMethod = cl.getMethod(transferMethod);
                        for (Object obj : objs) {
                            Object code = codeMethod.invoke(obj, new Object[0]);
                            if (value.toString().equals(code.toString())) {
                                log.debug("字段值 {} 匹配枚举类 {} 的{}()方法，校验通过", 
                                        value, cl.getName(), transferMethod);
                                return true;
                            }
                        }
                    } catch (NoSuchMethodException e) {
                        log.debug("枚举类 {} 没有{}方法，跳过该方法校验", 
                                cl.getName(), transferMethod);
                    }
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error("校验枚举值时发生异常: 枚举类={}, 字段值={}, 异常信息={}", 
                        cl.getName(), value, e.getMessage(), e);
            }
        }

        // 记录校验不通过的原因
        log.warn("字段值 {} 不在任何指定的枚举类中，校验不通过", value);
        return false;
    }
}
