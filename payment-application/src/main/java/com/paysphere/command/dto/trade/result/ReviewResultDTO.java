package com.paysphere.command.dto.trade.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResultDTO {

    /**
     * 审核结果
     */
    private Boolean reviewStatus;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reviewTime;

    /**
     * 审核说明
     */
    private String reviewMsg;

}
