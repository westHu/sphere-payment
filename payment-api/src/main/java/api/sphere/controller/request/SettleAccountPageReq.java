package api.sphere.controller.request;

import lombok.Data;

import java.util.List;


@Data
public class SettleAccountPageReq extends PageReq {

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
     * 账户号
     */
    private String accountNo;

}
