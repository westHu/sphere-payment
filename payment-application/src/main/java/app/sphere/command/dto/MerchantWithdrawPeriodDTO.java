package app.sphere.command.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantWithdrawPeriodDTO {

    /**
     * 结算周期类型
     */
    private String withdrawPeriod;

    /**
     * 金额
     */
    private BigDecimal limitAmount;

    /**
     * 时间
     */
    private String week;

    /**
     * 日期几号
     */
    private String date;

    /**
     * 时间时分秒
     */
    private String time;


}
