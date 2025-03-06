package com.paysphere.controller;


import cn.hutool.json.JSONUtil;
import com.paysphere.command.MerchantConfigCmdService;
import com.paysphere.command.cmd.MerchantConfigUpdateCmd;
import com.paysphere.command.cmd.PaymentLinkSettingCmd;
import com.paysphere.controller.request.MerchantConfigUpdateReq;
import com.paysphere.controller.request.MerchantIdReq;
import com.paysphere.controller.request.PaymentLinkSettingReq;
import com.paysphere.convert.MerchantConfigConverter;
import com.paysphere.query.MerchantConfigQueryService;
import com.paysphere.query.dto.MerchantConfigDTO;
import com.paysphere.query.dto.MerchantPaymentLinkSettingDTO;
import com.paysphere.query.param.MerchantIdParam;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * 商户基本配置Api
 */
@Slf4j
@RestController
public class MerchantConfigController {

    @Resource
    MerchantConfigQueryService merchantConfigQueryService;
    @Resource
    MerchantConfigCmdService merchantConfigCmdService;
    @Resource
    MerchantConfigConverter merchantConfigConverter;

    /**
     * 查询商户配置
     */
    @PostMapping("/v1/getMerchantConfig")
    public Mono<Result<MerchantConfigDTO>> getMerchantConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("getMerchantConfig req={}", JSONUtil.toJsonStr(req));
        MerchantIdParam param = merchantConfigConverter.convertMerchantIdParam(req);

        MerchantConfigDTO merchantConfigDTO = merchantConfigQueryService.getMerchantConfig(param);
        return Mono.just(Result.ok(merchantConfigDTO));
    }

    /**
     * 查询支付链接配置
     */
    @PostMapping("/v1/getPaymentLinkSetting")
    public Mono<Result<MerchantPaymentLinkSettingDTO>> getPaymentLinkSetting(@RequestBody @Validated MerchantIdReq req) {
        log.info("getPaymentLinkSetting req={}", JSONUtil.toJsonStr(req));
        MerchantIdParam param = merchantConfigConverter.convertMerchantIdParam(req);

        MerchantPaymentLinkSettingDTO paymentLinkSetting = merchantConfigQueryService.getPaymentLinkSetting(param);
        return Mono.just(Result.ok(paymentLinkSetting));
    }

    /**
     * 修改商户配置
     */
    @PostMapping("/v1/updateMerchantConfig")
    public Mono<Result<Boolean>> updateMerchantConfig(@RequestBody @Validated MerchantConfigUpdateReq req) {
        log.info("updateMerchantConfig req={}", JSONUtil.toJsonStr(req));
        MerchantConfigUpdateCmd cmd = merchantConfigConverter.convertMerchantConfigUpdateCmd(req);

        boolean updated = merchantConfigCmdService.updateMerchantConfig(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 更新支付链接配置
     */
    @PostMapping("/v1/updatePaymentLinkSetting")
    public Mono<Result<Boolean>> updatePaymentLinkSetting(@RequestBody @Validated PaymentLinkSettingReq req) {
        log.info("updatePaymentLinkSetting req={}", JSONUtil.toJsonStr(req));
        PaymentLinkSettingCmd cmd = merchantConfigConverter.convertPaymentLinkSettingCmd(req);

        boolean updated = merchantConfigCmdService.updatePaymentLinkSetting(cmd);
        return Mono.just(Result.ok(updated));
    }

}
