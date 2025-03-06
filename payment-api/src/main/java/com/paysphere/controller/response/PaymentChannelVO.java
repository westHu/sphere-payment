package com.paysphere.controller.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentChannelVO {

    /**
     * id
     */
    private Long id;

    /**
     * 对接的渠道编码
     * unique no
     */
    private String channelCode;

    /**
     * 对接的渠道名称
     * unique name
     */
    private String channelName;

    /**
     * 渠道类型
     */
    private String channelType;

    /**
     * 返回模式：0:收银台、1:收款号、4:都有
     */
    private String returnMode;

    /**
     * some url for api
     */
    private String url;

    /**
     * code\key
     */
    private String license;

    /**
     * 是否需要进件，创建子商户
     */
    private boolean division;

    /**
     * 0/1  Valid Invalid
     */
    private boolean status;

    /**
     * note something
     */
    private String remark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
