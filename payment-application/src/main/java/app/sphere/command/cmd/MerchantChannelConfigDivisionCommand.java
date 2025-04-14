package app.sphere.command.cmd;

import lombok.Data;

@Data
public class MerchantChannelConfigDivisionCommand {

    /**
     * 商户ID
     */
    private String merchantId;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 渠道分开账户（子账户）
     */
    private String channelDivision;


}
