package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantNotificationPageParam extends PageParam {

    /**
     * 标题
     */
    private String title;

    /**
     * 是否有效
     */
    private Boolean status;

}
