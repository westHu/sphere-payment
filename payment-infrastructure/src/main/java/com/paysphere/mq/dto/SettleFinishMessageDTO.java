package com.paysphere.mq.dto;

import lombok.Data;

import java.util.List;

@Data
public class SettleFinishMessageDTO {

    /**
     * 交易单号
     */
    private String tradeNo;

    /**
     * 结算单号
     */
    private String settleNo;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 结算状态
     */
    private Boolean settleStatus;

    /**
     * 结算时间
     */
    private String settleTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 账户余额信息
     */
    private List<SettleFinishAccountMessageDTO> accountList;

}
