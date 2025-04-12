package app.sphere.command.cmd;

import lombok.Data;

import java.util.List;

@Data
public class ChannelSwitchCommand {

    /**
     * 商户列表
     */
    private List<String> merchantIdList;

    /**
     * 交易方向 见枚举
     */
    private Integer paymentDirection;

    /**
     * 支付方式
     */
    private String paymentMethod;

    /**
     * 对接的渠道编码
     * unique no
     */
    private String channelCode;

    /**
     * 对接的渠道名称：duiTku、MidTrans、DANA...
     * unique name
     */
    private String channelName;

    /**
     * 交易类型
     */
    private Integer tradeType;

}
