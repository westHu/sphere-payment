package app.sphere.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SandboxTradePaymentLinkOrderPageDTO {

    /**
     * 支付链接单号
     */
    private String linkNo;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 币种
     */
    private String currency;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private Integer linkStatus;

    /**
     * 备注
     */
    private String notes;

    /**
     * 支付链接
     */
    private String paymentLink;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
