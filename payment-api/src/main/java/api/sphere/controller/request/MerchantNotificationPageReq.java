package api.sphere.controller.request;

import lombok.Data;

@Data
public class MerchantNotificationPageReq extends PageReq {

    /**
     * 标题
     */
    private String title;

    /**
     * 是否有效
     */
    private Boolean status;

}
