package com.paysphere.query.param;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class MerchantAgentListParam {

    /**
     * 代理商父ID
     */
    private String agentParentId;

    /**
     * 代理商ID
     */
    //@Length(min = 5, max = 5, message = "agentID must be 5-digit number")
    private String agentId;

    /**
     * 代理商名称
     */
    @Length(max = 64, message = "agentName max length 64-character")
    private String agentName;

    /**
     * 开始创建时间
     */
    private String startCreateTime;

    /**
     * 结束创建时间
     */
    private String endCreateTime;

}
