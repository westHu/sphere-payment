package api.sphere.controller.response;

import lombok.Data;

/**
 * 密码重置
 */
@Data
public class MerchantTradePasswordResetVO {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 新交易密码
     */
    private String newTradePassword;

}
