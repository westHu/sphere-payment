package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ChannelSwitchReq {

    /**
     * 商户列表
     */
    @NotEmpty(message = "merchantIdList is empty")
    private List<String> merchantIdList;

    /**
     * 交易方向 见枚举
     */
    @NotNull(message = "paymentDirection is required")
    private Integer paymentDirection;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 对接的渠道编码
     * unique no
     */
    @NotBlank(message = "channelCode is required")
    private String channelCode;

    /**
     * 对接的渠道名称：duiTku、MidTrans、DANA...
     * unique name
     */
    private String channelName;

    /**
     * 对接的渠道编码
     * unique no
     */
    @NotBlank(message = "targetChannelCode is required")
    private String targetChannelCode;

    /**
     * 对接的渠道名称：duiTku、MidTrans、DANA...
     * unique name
     */
    @NotBlank(message = "targetChannelName is required")
    private String targetChannelName;
}
