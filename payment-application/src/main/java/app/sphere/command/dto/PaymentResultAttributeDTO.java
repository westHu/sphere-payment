package app.sphere.command.dto;

import lombok.Data;

@Data
public class PaymentResultAttributeDTO {

    /**
     * 操作类型： supplement 补单； refund 退单...
     */
    private String type;

    /**
     * 操作员
     */
    private String operator;
}
