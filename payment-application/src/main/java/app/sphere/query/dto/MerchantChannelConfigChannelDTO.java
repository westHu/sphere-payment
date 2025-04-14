package app.sphere.query.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class MerchantChannelConfigChannelDTO {

    /**
     * ID
     */
    private Long id;

    /**
     * 渠道编码
     */
    private String channelCode;

    /**
     * 渠道名称
     */
    private String channelName;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 状态
     */
    private Boolean status;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MerchantChannelConfigChannelDTO that = (MerchantChannelConfigChannelDTO) o;
        return Objects.equals(channelCode, that.channelCode) && Objects.equals(channelName, that.channelName) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelCode, channelName, status);
    }
}
