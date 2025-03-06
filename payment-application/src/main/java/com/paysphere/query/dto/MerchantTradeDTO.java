package com.paysphere.query.dto;

import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.MerchantPaymentChannelConfig;
import com.paysphere.db.entity.MerchantPaymentConfig;
import com.paysphere.db.entity.MerchantPayoutChannelConfig;
import com.paysphere.db.entity.MerchantPayoutConfig;
import com.paysphere.db.entity.MerchantWithdrawChannelConfig;
import com.paysphere.db.entity.MerchantWithdrawConfig;
import lombok.Data;

@Data
public class MerchantTradeDTO {

    /**
     * 商户基本信息
     */
    private Merchant merchant;

    /**
     * 商户基本配置
     */
    private MerchantConfig merchantConfig;

    /**
     * 商户收款配置
     */
    private MerchantPaymentConfig merchantPaymentConfig;

    /**
     * 商户代付配置
     */
    private MerchantPayoutConfig merchantPayoutConfig;

    /**
     * 商户提现配置
     */
    private MerchantWithdrawConfig merchantWithdrawConfig;

    /**
     * 商户收款渠道配置 1对1
     */
    private MerchantPaymentChannelConfig merchantPaymentChannelConfig;

    /**
     * 商户代付渠道配置 1对1
     */
    private MerchantPayoutChannelConfig merchantPayoutChannelConfig;

    /**
     * 商户提现渠道配置 1对1
     */
    private MerchantWithdrawChannelConfig merchantWithdrawChannelConfig;

}
