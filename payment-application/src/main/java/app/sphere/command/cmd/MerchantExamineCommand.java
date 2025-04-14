package app.sphere.command.cmd;

import share.sphere.enums.MerchantTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantExamineCommand {

    /**
     * 商户号
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户类型
     *
     * @see MerchantTypeEnum
     */
    @NotNull(message = "merchantType is required")
    private Integer merchantType;

    /**
     * 个人
     */
    private MerchantPersonCommand person;

    /**
     * 企业
     */
    private MerchantEnterpriseCommand enterprise;

    /**
     * 商户提现配置
     */
    private MerchantWithdrawConfigCommand merchantWithdrawConfig;
}
