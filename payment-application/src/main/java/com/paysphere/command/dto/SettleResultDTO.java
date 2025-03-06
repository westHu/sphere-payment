package com.paysphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class SettleResultDTO {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 错误消息
     */
    private String errorMsg;

    /**
     * 账户余额
     */
    private List<AccountDTO> accountList;
}
