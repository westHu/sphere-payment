package app.sphere.query.param;

import lombok.Data;

@Data
public class MerchantNotificationParam {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 标题
     */
    private String title;

    /**
     * 是否已阅读
     */
    private Boolean read;

    /**
     * 是否有效
     */
    private Boolean status;

}
