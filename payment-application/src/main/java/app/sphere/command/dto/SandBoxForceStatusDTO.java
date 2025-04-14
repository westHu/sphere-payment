package app.sphere.command.dto;

import lombok.Data;

@Data
public class SandBoxForceStatusDTO {

    /**
     * 设置的支付状态
     */
    private String paymentStatus;

    /**
     * 回调地址
     */
    private String callBackUrl;

    /**
     * 回调状态
     */
    private boolean callBackStatus;

    /**
     * 回调结果
     */
    private String callBackResult;

}
