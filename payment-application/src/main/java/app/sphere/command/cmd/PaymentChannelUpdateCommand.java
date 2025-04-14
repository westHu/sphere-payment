package app.sphere.command.cmd;

import lombok.Data;

@Data
public class PaymentChannelUpdateCommand {

    /**
     * ID
     */
    private Long id;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * API地址
     */
    private String url;

    /**
     * 授权
     */
    private String license;
}
