package com.paysphere.command.impl;

import com.paysphere.command.MerchantIndustryTemplateCmdService;
import com.paysphere.repository.MerchantTemplatePaymentChannelConfigService;
import com.paysphere.repository.MerchantTemplatePayoutChannelConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MerchantIndustryTemplateCmdServiceImpl implements MerchantIndustryTemplateCmdService {

    @Resource
    MerchantTemplatePaymentChannelConfigService merchantTemplatePaymentChannelConfigService;
    @Resource
    MerchantTemplatePayoutChannelConfigService merchantTemplatePayoutChannelConfigService;


}
