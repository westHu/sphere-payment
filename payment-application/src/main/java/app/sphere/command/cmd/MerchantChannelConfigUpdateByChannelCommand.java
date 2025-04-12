package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantChannelConfigUpdateByChannelCommand {

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 商户渠道配置状态
     */
    private Boolean status;

}
