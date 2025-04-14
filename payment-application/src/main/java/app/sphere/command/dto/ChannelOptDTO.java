package app.sphere.command.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChannelOptDTO {

    /**
     * 成功数量
     */
    private Integer successCount;

    /**
     * 失败数量
     */
    private Integer failedCount;

    /**
     * 失败列表
     */
    private List<String> failedMerchantIdList;
}
