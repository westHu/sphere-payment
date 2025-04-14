package api.sphere.controller.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class MerchantNotificationAddReq {

    /**
     * 通知类型
     */
    private Integer notifyType;

    /**
     * 被通知者
     */
    @Length(max = 32, message = "notifyPerson max length 32-character")
    private String notifyPerson;

    /**
     * 通知标题
     */
    @Length(max = 32, message = "notifyTitle max length 32-character")
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
