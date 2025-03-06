package com.paysphere.controller.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MerchantNotificationVO {

    /**
     * ID
     */
    private Long id;

    /**
     * 通知类型
     */
    private Integer notifyType;

    /**
     * 被通知者
     */
    private String notifyPerson;

    /**
     * 通知标题
     */
    private String notifyTitle;

    /**
     * 通知内容
     */
    private String notifyContent;

    /**
     * 紧急程度/排序
     */
    private Integer urgency;

    /**
     * 通知时间
     */
    private LocalDateTime noticeTime;

    /**
     * 通知有效期
     */
    private Integer noticePeriod;

    /**
     * 阅读状态
     */
    private Boolean readStatus;

    /**
     * 阅读时间
     */
    private LocalDateTime readTime;

    /**
     * 通知状态
     */
    private Boolean status;

}
