package api.sphere.controller.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MerchantChannelConfigListReq extends MerchantQuerySourceReq {

    /**
     * 状态
     */
    private Boolean status;

}
