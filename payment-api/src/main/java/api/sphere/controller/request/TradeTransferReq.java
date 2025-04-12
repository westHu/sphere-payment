package api.sphere.controller.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Data
public class TradeTransferReq {

    /**
     * 转账目的
     */
    @Length(max = 60, message = "purpose|remark length max 60")
    private String purpose;

    /**
     * 转出商户ID
     */
    private String fromMerchantId;

    /**
     * 转出商户名称
     */
    private String fromMerchantName;

    /**
     * 转出账户
     */
    private String fromAccountNo;

    /**
     * 转入商户ID
     */
    private String toMerchantId;

    /**
     * 转入商户名称
     */
    private String toMerchantName;

    /**
     * 转入账户
     */
    private String toAccountNo;


    /**
     * 币种
     */
    @NotBlank(message = "currency is required")
    private String currency;

    /**
     * 转账金额
     */
    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.00", message = "amount should greater zero")
    private BigDecimal amount;

    /**
     * 申请人
     */
    @NotBlank(message = "applyOperator is required")
    private String applyOperator;

}
