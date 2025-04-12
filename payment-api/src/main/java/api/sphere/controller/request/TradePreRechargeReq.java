package api.sphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
public class TradePreRechargeReq {

    /**
     * 地区
     */
    private Integer area;

    /**
     * 充值目的
     */
    @Length(max = 32, message = "purpose|remark length max 32")
    private String purpose;

    /**
     * 充值商户ID
     */
    @NotBlank(message = "merchantId is required")
    private String merchantId;

    /**
     * 充值商户名称
     */
    @NotBlank(message = "merchantName is required")
    private String merchantName;

    /**
     * 充值账户
     */
    @NotBlank(message = "accountNo is required")
    private String accountNo;

    /**
     * 充值币种 - 用户充值的原始币种
     */
    private String rechargeCurrency;

    /**
     * 充值金额 - 用户充值的原始金额（以充值币种计算）
     */
    private BigDecimal rechargeAmount;

    /**
     * 支付方式
     */
    @NotNull(message = "paymentMethod is required")
    private String paymentMethod;


}
