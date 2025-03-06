package com.paysphere.mq.dto.tg;

import lombok.Data;

@Data
public class TgJobMonitorDTO {

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务状态
     */
    private String jobStatus;

    /**
     * 任务描述
     */
    private String jobDesc;


}
