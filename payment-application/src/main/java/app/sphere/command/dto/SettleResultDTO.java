package app.sphere.command.dto;

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
}
