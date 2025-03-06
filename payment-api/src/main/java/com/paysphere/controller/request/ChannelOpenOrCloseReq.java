package com.paysphere.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ChannelOpenOrCloseReq {

    /**
     * 商户列表
     */
    @NotEmpty(message = "merchantIdList is empty")
    private List<String> merchantIdList;

    /**
     * 开关
     */
    @NotNull(message = "onOff is required")
    private Boolean onOff;

    /**
     * 交易方向 见枚举
     */
    @NotNull(message = "paymentDirection is required")
    private Integer paymentDirection;

    /**
     * 支付方式
     */
    @NotBlank(message = "paymentMethod is required")
    private String paymentMethod;

    /**
     * 渠道编码
     */
    @NotBlank(message = "channelCode is required")
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;


}
