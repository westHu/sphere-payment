package app.sphere.command.dto;

import lombok.Data;

@Data
public class AccountApplyBncDTO {

    /**
     * 附件条件
     */
    private AccountBncAdditionInfoDTO additionInfo;

    /**
     * 业务信息
     */
    private AccountBncBusinessInfoDTO businessInfo;

    /**
     * 商家身份
     * 1" sub-merchant 2 store
     */
    private String merchantIdentity = "1";

    /**
     * serviceCode
     */
    private String serviceCode = "Q21";

    /**
     * 结算账户信息
     */
    private AccountBncSettlementAccountInfoDTO settlementAccountInfo;

    /**
     * 结算信息
     */
    private AccountBncSettlementInfoDTO settlementInfo;

    /**
     * subject信息
     */
    private AccountBncSubjectInfoDTO accountInfo;

}
