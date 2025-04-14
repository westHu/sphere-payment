package api.sphere.controller.request;

import api.sphere.config.EnumValid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import share.sphere.enums.AccountTypeEnum;


@Data
public class SettleAccountAddReq {

    /**
     * 商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 商户名称
     */
    @NotBlank(message = "merchantName is required")
    private String merchantName;

    /**
     * 账户类型
     */
    @NotNull(message = "accountType is null")
    @EnumValid(target = AccountTypeEnum.class, transferMethod = "getCode", message = "accountType type not support")
    private Integer accountType;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    @NotBlank(message = "accountName is required")
    private String accountName;

    /**
     * 币种
     */
    @NotBlank(message = "currency is required")
    private String currency;

}
