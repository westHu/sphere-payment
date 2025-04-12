package app.sphere.command.cmd;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MerchantWithdrawConfigUpdateCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 提现到哪
     */
    private String withdrawTo;

    /**
     * 提现周期
     */
    private String withdrawPeriod;

    /**
     * 提现提醒邮件
     */
    private String withdrawPost;

    /**
     * 提现费用
     */
    private BigDecimal withdrawFee;

    /**
     * 提现费率
     */
    private BigDecimal withdrawRate;

}
