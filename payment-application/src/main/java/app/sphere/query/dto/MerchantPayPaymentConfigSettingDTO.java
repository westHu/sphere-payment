package app.sphere.query.dto;

import lombok.Data;

import java.util.List;

@Data
public class MerchantPayPaymentConfigSettingDTO {

    /**
     * 收款支付渠道配置
     */
    private List<MerchantPaymentChannelConfigDTO> merchantPayPaymentConfigDTOList;
    /**
     * 支付链接配置
     */
    private MerchantPaymentLinkSettingDTO merchantPaymentLinkSetting;

}
