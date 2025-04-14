package app.sphere.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import share.sphere.TradeConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TradeTransferOrderDetailDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 结算状态
     */
    private Integer settleStatus;

    /**
     * 转账商户ID
     */
    private String merchantId;

    /**
     * 转账商户名称
     */
    private String merchantName;

    /**
     * 转账账户
     */
    private String accountNo;

    /**
     * 币种
     */
    private String currency;

    /**
     * 转账金额
     */
    private BigDecimal amount;

    /**
     * 转账目的
     */
    private String purpose;

    /**
     * 交易时间
     */
    @JsonFormat(pattern = TradeConstant.FORMATTER_0)
    private LocalDateTime tradeTime;

    /**
     * 结算完成时间
     */
    @JsonFormat(pattern = TradeConstant.FORMATTER_0)
    private LocalDateTime finishSettleTime;

}
