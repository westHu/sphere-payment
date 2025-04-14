package app.sphere.query.dto;

import lombok.Data;

/**
 * @Author Moore
 * @Date 2024/6/22 11:09
 **/
@Data
public class PayerDTO {

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 地址
     */
    private String address;

    /**
     * 身份ID
     */
    private String identity;

    /**
     * 付款方式
     */
    private String useMethod;
}
