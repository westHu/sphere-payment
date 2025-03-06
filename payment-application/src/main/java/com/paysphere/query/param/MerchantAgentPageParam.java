package com.paysphere.query.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantAgentPageParam extends PageParam {

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
