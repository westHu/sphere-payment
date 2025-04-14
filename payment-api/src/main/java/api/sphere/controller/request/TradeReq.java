package api.sphere.controller.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


@Data
public class TradeReq {

    /**
     * 地区
     */
    private String region;

    /**
     * 商户平台订单号
     */
    @NotBlank(message = "orderNo is required")
    @Length(max = 64, message = "orderNo length max 64")
    private String orderNo;

    /**
     * 目的
     */
    @NotBlank(message = "purpose is required")
    @Length(max = 64, message = "purpose length max 64")
    private String purpose;

    /**
     * 商品详情
     */
    @Length(max = 128, message = "productDetail length max 128")
    private String productDetail;

    /**
     * 金额
     */
    @NotNull(message = "money is required")
    @Valid
    private MoneyReq money;

    /**
     * 商户信息
     */
    @NotNull(message = "merchant is required")
    @Valid
    private MerchantReq merchant;

    /**
     * 回调地址
     */
    private String callbackUrl;

    /**
     * 跳转地址
     */
    private String redirectUrl;

    /**
     * 附件信息
     */
    private String additionalInfo;

    /**
     * 来源
     */
    private Integer source;
}
