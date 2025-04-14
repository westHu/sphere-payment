package app.sphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class MerchantQrisConfigApiCommand {

    /**
     * 商户
     */
    private MerchantIdCommand merchant;

    /**
     * 标题
     */
    private String shopName;

    /**
     * qrisFrom
     */
    private List<String> qrisFrom;

    /**
     * WhatsApp 电话
     */
    private String whatsAppNotification;

    /**
     * 附件信息
     */
    private String additionalInfo;

}
