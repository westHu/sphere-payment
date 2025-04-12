package app.sphere.command.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeCallBackResultAttributeDTO {

    /**
     * url
     */
    private String url;

    /**
     * 参数
     */
    private String param;

    /**
     * 操作员
     */
    private String operator;
}
