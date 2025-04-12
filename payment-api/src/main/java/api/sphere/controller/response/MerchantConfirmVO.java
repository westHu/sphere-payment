package api.sphere.controller.response;

import lombok.Data;

@Data
public class MerchantConfirmVO {

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 确认结果
     */
    private Boolean result;

    /**
     * 消息
     */
    private String message;

}
