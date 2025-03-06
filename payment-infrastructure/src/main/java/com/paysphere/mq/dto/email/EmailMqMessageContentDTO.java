package com.paysphere.mq.dto.email;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class EmailMqMessageContentDTO {

    /**
     * 邮件内容格式: 文本\HTML\附件\模版\...
     * 如果文本: text
     * 如果 HTML: html
     * 如果附件: text\attachments
     * 如果模版: templateName\templateParam
     */
    @NotBlank(message = "contentType is required")
    private String contentType;

    /**
     * 文本内容
     */
    private String text;

    /**
     * 文本内容
     */
    private String html;

    /**
     * 文件路径, MQ不能传流,需要文件路径
     */
    private List<String> attachments;

    /**
     * 模版类型
     */
    private String templateName;

    /**
     * 模版参数 json
     */
    private String templateParam;

}
