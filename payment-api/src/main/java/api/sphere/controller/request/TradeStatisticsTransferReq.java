package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class TradeStatisticsTransferReq extends PageReq {

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
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户类型
     */
    private List<Integer> accountTypeList;

    /**
     * 转账方向
     */
    private Integer transferDirection;

}
