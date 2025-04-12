package app.sphere.command.dto.trade.callback;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TradeCallBackDTO {

    /**
     * 模式
     */
    @NotBlank(message = "mode is required")
    private String mode;

    /**
     * 来源
     */
    private String source;

    /**
     * 回调地址
     */
    @NotBlank(message = "url is required")
    private String url;

    /**
     * 回调内容
     */
    @NotNull(message = "body is required")
    private TradeCallBackBodyDTO body;

}
