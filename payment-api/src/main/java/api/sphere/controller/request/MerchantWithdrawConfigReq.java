package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantWithdrawConfigReq {

    /**
     * 提现方式
     */
    @NotBlank(message = "withdrawPaymentMethod is required")
    @Length(max = 32, message = "withdrawPaymentMethod length max 32")
    private String withdrawPaymentMethod;

    /**
     * 提现方式名称
     */
    @NotBlank(message = "withdrawPaymentName is required")
    private String withdrawPaymentName;

    /**
     * 提现账户
     */
    @NotBlank(message = "cashAccount is required")
    @Length(max = 32, message = "withdrawAccount length max 32")
    private String withdrawAccount;
}
