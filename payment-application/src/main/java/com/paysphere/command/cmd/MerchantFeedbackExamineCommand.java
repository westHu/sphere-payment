package com.paysphere.command.cmd;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 进件材料
 */
@Data
public class MerchantFeedbackExamineCommand {

    /**
     * 商户号
     */
    @NotBlank(message = "merchantId is required")
    @Length(max = 16, message = "merchantId is too long")
    private String merchantId;

    /**
     * 行业
     */
    private String industry;

    /**
     * 进件结果
     */
    @NotBlank(message = "examineStatus is required")
    private Boolean examineStatus;

    /**
     * 进件说明
     */
    private String examineComment;

}
