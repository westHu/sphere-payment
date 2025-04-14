package app.sphere.query.param;

import lombok.Data;

@Data
public class CashierParam {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 秘钥
     */
    private String token;
}
