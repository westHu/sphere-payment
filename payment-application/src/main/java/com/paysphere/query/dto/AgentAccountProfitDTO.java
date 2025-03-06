package com.paysphere.query.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AgentAccountProfitDTO {

    /**
     * 代理商余额
     */
    private AgentAccountBalanceDTO agentAccountBalance = new AgentAccountBalanceDTO();

    /**
     * 代理商当日分润佣金
     */
    private AgentAccountTodayProfitDTO agentAccountTodayProfit = new AgentAccountTodayProfitDTO();

    /**
     * 代理商余额快照列表
     */
    private List<AgentAccountSnapshotProfitDTO> agentAccountSnapshotProfitList = new ArrayList<>();

    /**
     * 代理商流水列表
     */
    private List<AgentAccountFlowDTO> agentAccountFlowList = new ArrayList<>();

}
