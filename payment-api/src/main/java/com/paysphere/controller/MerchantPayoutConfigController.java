package com.paysphere.controller;


import cn.hutool.json.JSONUtil;
import com.paysphere.controller.request.MerchantIdReq;
import com.paysphere.query.MerchantPayoutConfigQueryService;
import com.paysphere.query.dto.MerchantPayoutConfigDTO;
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
 * 商户代付配置Api
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class MerchantPayoutConfigController {

    @Resource
    MerchantPayoutConfigQueryService merchantPayoutConfigQueryService;

    /**
     * 查询商户代付配置
     */
    @PostMapping("/v1/getMerchantPayoutConfig")
    public Mono<Result<MerchantPayoutConfigDTO>> getMerchantPayoutConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("getMerchantPayoutConfig req={}", JSONUtil.toJsonStr(req));
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());

        MerchantPayoutConfigDTO merchantPayoutConfig = merchantPayoutConfigQueryService.getMerchantPayoutConfig(param);
        return Mono.just(Result.ok(merchantPayoutConfig));
    }


}
