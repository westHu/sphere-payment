package app.sphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class MerchantNotificationAddCommand {

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
     * 图标
     */
    private String icon;

    /**
     * 关键字
     */
    private List<String> keywords;

    /**
     * 链接
     */
    private String link;

    /**
     * 图片
     */
    private String img;

    /**
     * 内容
     */
    private String content;

    /**
     * 紧急程度
     */
    private Integer urgency;

}
