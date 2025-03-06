package com.paysphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class SettleAccountPageParam extends PageParam {

    /**
     * 账户类型
     */
    private Integer accountType;

    /**
     * 账户类型
     */
    private List<Integer> accountTypeList;

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 子商户ID
     */
    private String subMerchantId;

    /**
     * 子商户名称
     */
    private String subMerchantName;

    /**
     * 账户号
     */
    private String accountNo;

    /**
     * 角色（地区）
     */
    private Integer role;

    /**
     * 操作人
     */
    private String operator;

    /**
     * 来源系统
     */
    private String source;
}
