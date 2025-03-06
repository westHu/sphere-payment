package com.paysphere.command.dto;

import lombok.Data;

@Data
public class OperateDTO {

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作员
     */
    private String operator;

    /**
     * 操作时间
     */
    private String operatorTime;
}
