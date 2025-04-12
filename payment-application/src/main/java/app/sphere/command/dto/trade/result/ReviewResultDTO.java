package app.sphere.command.dto.trade.result;

import lombok.Data;

@Data
public class ReviewResultDTO {

    /**
     * 审核结果
     */
    private Boolean reviewStatus;

    /**
     * 审核时间
     */
    private Long reviewTime;

    /**
     * 审核说明
     */
    private String reviewMsg;

}
