package api.sphere.controller.request;

import api.sphere.config.EnumValid;
import share.sphere.enums.MerchantTypeEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * 进件材料
 */
@Data
public class MerchantExamineReq {

    /**
     * 商户号
     */
    @NotBlank(message = "merchantId is required")
    @Length(max = 16, message = "merchantId max length 16")
    private String merchantId;

    /**
     * 商户类型
     *
     * @see MerchantTypeEnum
     */
    @NotNull(message = "merchantType is required")
    @EnumValid(target = MerchantTypeEnum.class, transferMethod = "getCode", message = "merchant type not support")
    private Integer merchantType;

    /**
     * 个人
     */
    private MerchantPersonReq person;

    /**
     * 企业
     */
    private MerchantEnterpriseReq enterprise;

    /**
     * 商户提现配置
     */
    @NotNull(message = "merchantWithdrawConfig is required")
    @Valid
    private MerchantWithdrawConfigReq merchantWithdrawConfig;


}
