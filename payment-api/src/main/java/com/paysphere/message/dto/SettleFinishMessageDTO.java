package com.paysphere.message.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class SettleFinishMessageDTO {

    /**
     * 交易单号
     */
    @NotBlank(message = "tradeNo is required")
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
    private boolean settleStatus;

    /**
     * 结算时间
     */
    @NotBlank(message = "settleTime is required")
    private String settleTime;

    /**
     * 账户余额信息
     */
    private List<SettleFinishAccountMessageDTO> accountList;
}
