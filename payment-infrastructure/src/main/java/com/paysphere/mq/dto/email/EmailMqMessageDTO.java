package com.paysphere.mq.dto.email;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @Author West
 * @Date 2023/10/7 15:06
 **/

@Data
public class EmailMqMessageDTO {

    /**
     * 接收者. 多个请用","分隔
     */
    @NotBlank(message = "sendTo is required")
    private String sendTo;

    /**
     * 抄送者. 多个请用","分隔
     */
    private String ccTo;

    /**
     * 主题
     */
    @NotBlank(message = "subject is required")
    private String subject;


    /**
     * 内容
     */
    @NotBlank(message = "content is required")
    @Valid
    private EmailMqMessageContentDTO content;

}
