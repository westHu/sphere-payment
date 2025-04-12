package app.sphere.query.dto;

import lombok.Data;

@Data
public class AccountBalanceApiQueryDTO {

    /**
     * 商户ID
     */
    private String merchantId;


    /**
     * 商户名称
     */
    private String merchantName;


    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 账户名称
     */
    private String accountName;

    /**
     * 总余额
     */
    private AccountBalanceDTO amount;

    /**
     * 可用余额
     */
    private AccountBalanceDTO availableBalance;

    /**
     * 扩展
     */
    private String additionalInfo;

}
