package infrastructure.sphere.remote;


import lombok.Data;
import share.sphere.enums.CurrencyEnum;

import java.math.BigDecimal;

@Data
public class BaseInquiryBalanceDTO {

    /**
     * 币种
     */
    private String currency = CurrencyEnum.IDR.name();

    /**
     * 余额
     */
    private BigDecimal balance = BigDecimal.ZERO;

    /**
     * 可用余额
     */
    private BigDecimal availableBalance = BigDecimal.ZERO;

    /**
     * 处理中金额
     */
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 接口返回数据
     */
    private String orgData;

}
