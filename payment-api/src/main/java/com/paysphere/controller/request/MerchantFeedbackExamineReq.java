package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 进件材料
 */
@Data
public class MerchantFeedbackExamineReq {

    /**
     * 商户号
     */
    @NotBlank(message = "merchantId is required")
    @Length(max = 16, message = "merchantId max length 32")
    private String merchantId;

    /**
     * 进件结果
     */
    @NotNull(message = "examineStatus is required")
    private Boolean examineStatus;

    /**
     * 进件消息
     */
    private String examineComment;

}
