package api.sphere.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 枚举值校验注解
 * 用于校验字段值是否在指定的枚举类中
 * 
 * 使用示例：
 * @EnumValid(target = CurrencyEnum.class, message = "currency is not support")
 * private String currency;
 * 
 * 校验规则：
 * 1. 如果字段值为null，则校验通过
 * 2. 如果字段值为空字符串，则校验通过
 * 3. 如果字段值不为null且不为空字符串，则校验该值是否在指定的枚举类中
 * 4. 校验时会同时检查枚举的name()方法和指定的transferMethod()方法
 * 
 * 日志记录：
 * 1. 校验不通过时，记录详细的错误信息，包括：
 *    - 字段名
 *    - 字段值
 *    - 枚举类名
 *    - 允许的枚举值列表
 * 2. 校验通过时，记录简单的通过信息
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {EnumValidator.class})
@Documented
public @interface EnumValid {

    /**
     * 错误提示信息
     * 当校验不通过时，返回给用户的错误信息
     */
    String message() default "";

    /**
     * 分组校验
     * 用于分组校验，可以指定多个分组
     */
    Class<?>[] groups() default {};

    /**
     * 负载
     * 用于传递额外的校验信息
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 目标枚举类
     * 指定要校验的枚举类，可以指定多个枚举类
     */
    Class<?>[] target() default {};

    /**
     * 转换方法
     * 指定枚举类中用于转换的方法名，默认为"name"
     * 例如：如果枚举类中有getCode()方法，可以指定transferMethod = "getCode"
     */
    String transferMethod() default "name";
}
