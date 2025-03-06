package com.paysphere.command.dto;

import lombok.Data;

@Data
public class TradeTransferAttributeDTO {

    /**
     * 申请人
     */
    private String applyOperator;

    /**
     * 出款账户冻结结果
     */
    private String frozenAccountResult;

}
