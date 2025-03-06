package com.paysphere.command.cmd;

import com.paysphere.enums.AgentStatusEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.util.List;

@Data
public class MerchantAgentDetailUpdateCommand {

    /**
     * 代理商名称
     */
    @NotBlank(message = "agentId is required")
    private String agentId;

    /**
     * 代理商电话
     */
    @Length(max = 64, message = "agentPhone max length 64-character")
    private String agentPhone;

    /**
     * 代理商邮箱
     */
    @Length(max = 64, message = "agentEmail max length 64-character")
    private String agentEmail;

    /**
     * 代理商头像
     */
    @Length(max = 64, message = "agentAvatar max length 64-character")
    private String agentAvatar;

    /**
     * 状态
     *
     * @see AgentStatusEnum
     */
    private Integer status;

    /**
     * 收款费率
     */
    private List<MerchantAgentPayPaymentConfigCommand> merchantAgentPayPaymentConfigList;

    /**
     * 代付费率
     */
    private List<MerchantAgentCashPaymentConfigCommand> merchantAgentCashPaymentConfigList;

}
