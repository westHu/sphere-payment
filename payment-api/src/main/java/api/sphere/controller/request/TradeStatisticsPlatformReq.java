package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TradeStatisticsPlatformReq extends PageReq {

    /**
     * 交易开始日期
     */
    @NotBlank(message = "tradeStartDate is required")
    private String tradeStartDate;

    /**
     * 交易结束日期
     */
    @NotBlank(message = "tradeEndDate is required")
    private String tradeEndDate;

    /**
     * 交易类型
     */
    private Integer tradeType;

}
