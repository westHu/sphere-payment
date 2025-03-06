package com.paysphere.query.param;

import lombok.Data;

@Data
public class MerchantAgentGroupParam {

    /**
     * 代理商父ID
     */
    private String agentParentId;

    /**
     * 代理商ID
     */
    private String agentId;

    /**
     * 代理商名称
     */
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
