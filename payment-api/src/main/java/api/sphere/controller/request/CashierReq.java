package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CashierReq {

    /**
     * 订单交易号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 秘钥
     */
    private String token;

}
