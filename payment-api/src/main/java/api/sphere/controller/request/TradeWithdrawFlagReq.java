package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeWithdrawFlagReq {

    /**
     * 商户号
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 提现日期
     */
    @NotBlank(message = "withdrawDate is required")
    private String withdrawDate;

}
