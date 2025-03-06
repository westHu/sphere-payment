package com.paysphere.mq.dto.lark;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author Moore
 * @Date 2023/10/7 15:06
 **/

@Data
public class LarkMqMessageDTO {

    /**
     * 应用名称
     */
    @NotBlank(message = "applicationName is required")
    private String applicationName;

    /**
     * 消息类型
     */
    @NotBlank(message = "messageType is required")
    private String messageType;

    /**
     * 消息组群
     */
    private String group;

    /**
     * 消息头
     */
    private String header;

    /**
     * 消息描述信息
     */
    private String message;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 消息体 多变量，封装成object 转成json
     */
    private String templateParam;

}
