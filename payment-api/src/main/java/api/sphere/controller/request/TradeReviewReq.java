package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class TradeReviewReq {

    /**
     * 交易单号
     */
    @NotBlank(message = "tradeNo is required")
    private String tradeNo;

    /**
     * 审核状态
     */
    @NotNull(message = "reviewStatus is required")
    private Boolean reviewStatus;

    /**
     * 审核意见
     */
    @Length(max = 128, message = "reviewMsg max length 128")
    private String reviewMsg;
}
