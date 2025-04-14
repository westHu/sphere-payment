package app.sphere.command.dto;

import lombok.Data;

@Data
public class MerchantAttributeDTO {

    /**
     * 新增的人
     */
    private String addBy;

    /**
     * 审核的人
     */
    private String verifyBy;
}
