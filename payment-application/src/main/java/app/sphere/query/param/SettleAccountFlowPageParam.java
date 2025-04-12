package app.sphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SettleAccountFlowPageParam extends PageParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 账户账号
     */
    private String accountNo;

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 账户资金方向 1 收入 -1 支出
     */
    private Integer accountDirection;

}
