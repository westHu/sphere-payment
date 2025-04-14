package infrastructure.sphere.remote.channel;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ChannelParam {

    /**
     * 请求类型
     * application/json
     * application/x-www-form-urlencoded
     */
    @Builder.Default
    private String mediaType = MediaTypeEnum.JSON.getType();

    /**
     * 请求地址
     */
    private String url;

    /**
     * 参数json
     */
    private String req;

    /**
     * 请求头
     */
    private Map<String, String> headerMap;

    /**
     * 返回 HttpCode
     */
    @Builder.Default
    private int httpCode = 200;

    /**
     * 连接超时
     */
    @Builder.Default
    private long connectTimeout = 15L;

    /**
     * 读超时
     */
    @Builder.Default
    private long readTimeout = 15L;

    /**
     * 交易单号，方便定位问题
     */
    private String tradeNo;

}
