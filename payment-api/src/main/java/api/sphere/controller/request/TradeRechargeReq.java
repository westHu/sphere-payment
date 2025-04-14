package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeRechargeReq {

    /**
     * 充值单号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 充值状态
     */
    private boolean status;

    /**
     * 证明 譬如图片截图
     */
    private String proof;


}
