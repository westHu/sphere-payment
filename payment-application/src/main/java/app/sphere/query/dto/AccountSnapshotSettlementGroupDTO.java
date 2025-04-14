package app.sphere.query.dto;

import lombok.Data;

@Data
public class AccountSnapshotSettlementGroupDTO {

    /**
     * 日期
     */
    private String accountDate;

    /**
     * 商户账户正常数量
     */
    private Integer merchantAccountNormalCount = 0;

    /**
     * 商户账户异常数量
     */
    private Integer merchantAccountAbnormalCount = 0;

    /**
     * 平台账户正常数量
     */
    private Integer platformAccountNormalCount = 0;

    /**
     * 平台账户异常数量
     */
    private Integer platformAccountAbnormalCount = 0;

    /**
     * 状态
     */
    private boolean status;

}
