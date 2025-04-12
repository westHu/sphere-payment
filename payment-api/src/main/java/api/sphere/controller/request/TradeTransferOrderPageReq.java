package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TradeTransferOrderPageReq extends PageReq {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 转账商户ID
     */
    private String merchantId;

    /**
     * 转账账户
     */
    private String accountNo;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 交易开始时间
     */
    @NotBlank(message = "tradeStartTime is required")
    private String tradeStartTime;

    /**
     * 交易结束时间
     */
    @NotBlank(message = "tradeEndTime is required")
    private String tradeEndTime;

}
