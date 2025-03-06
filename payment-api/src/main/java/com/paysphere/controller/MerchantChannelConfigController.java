package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.MerchantChannelConfigCmdService;
import com.paysphere.command.cmd.MerchantChannelConfigUpdateCmd;
import com.paysphere.command.cmd.MerchantIdCommand;
import com.paysphere.controller.request.MerchantChannelConfigListReq;
import com.paysphere.controller.request.MerchantChannelConfigUpdateReq;
import com.paysphere.controller.request.MerchantIdReq;
import com.paysphere.convert.MerchantChannelConfigConverter;
import com.paysphere.query.MerchantChannelConfigQueryService;
import com.paysphere.query.dto.MerchantChannelConfigListDTO;
import com.paysphere.query.param.MerchantChannelConfigListParam;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 商户渠道配置API
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class MerchantChannelConfigController {

    @Resource
    MerchantChannelConfigQueryService merchantChannelConfigQueryService;
    @Resource
    MerchantChannelConfigCmdService merchantChannelConfigCmdService;
    @Resource
    MerchantChannelConfigConverter merchantChannelConfigConverter;

    /**
     * 查询某商户渠道配置 - 商户平台/管理平台
     */
    @PostMapping("/v1/getMerchantChannelConfigList")
    public Mono<Result<MerchantChannelConfigListDTO>> getMerchantChannelConfigList(@RequestBody @Validated MerchantChannelConfigListReq req) {
        log.info("getMerchantChannelConfigList param={}", JSONUtil.toJsonStr(req));
        MerchantChannelConfigListParam param =
                merchantChannelConfigConverter.convertMerchantChannelConfigListParam(req);

        MerchantChannelConfigListDTO configList = merchantChannelConfigQueryService.getMerchantChannelConfigList(param);
        return Mono.just(Result.ok(configList));
    }

    /**
     * 更新状态 - 管理平台
     */
    @PostMapping("/v1/updateMerchantChannelStatus")
    public Mono<Result<Boolean>> updateMerchantChannelStatus(@RequestBody @Validated MerchantChannelConfigUpdateReq req) {
        log.info("MerchantChannel updateMerchantChannelStatus req={}", JSONUtil.toJsonStr(req));
        MerchantChannelConfigUpdateCmd cmd = merchantChannelConfigConverter.convertMerchantChannelConfigUpdateCmd(req);

        boolean updated = merchantChannelConfigCmdService.updateMerchantChannelStatus(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 更新优先级 - 管理平台
     */
    @PostMapping("/v1/updateMerchantChannelPriority")
    public Mono<Result<Boolean>> updateMerchantChannelPriority(@RequestBody @Validated MerchantChannelConfigUpdateReq req) {
        log.info("MerchantChannel updateMerchantChannelPriority req={}", JSONUtil.toJsonStr(req));
        MerchantChannelConfigUpdateCmd cmd = merchantChannelConfigConverter.convertMerchantChannelConfigUpdateCmd(req);

        boolean updated = merchantChannelConfigCmdService.updateMerchantChannelPriority(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 更新费用 限额 结算周期等 - 管理平台
     */
    @PostMapping("/v1/updateMerchantChannelFee")
    public Mono<Result<Boolean>> updateMerchantChannelFee(@RequestBody @Validated MerchantChannelConfigUpdateReq req) {
        log.info("MerchantChannel updateMerchantChannelConfig req={}", JSONUtil.toJsonStr(req));
        MerchantChannelConfigUpdateCmd cmd = merchantChannelConfigConverter.convertMerchantChannelConfigUpdateCommand(req);

        boolean updated = merchantChannelConfigCmdService.updateMerchantChannelFee(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 同步商户配置 * 从模版同步到商户
     */
    @PostMapping("/v1/syncMerchantChannelConfig")
    public Mono<Result<Boolean>> syncMerchantChannelConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("syncMerchantChannelConfig req={}", JSONUtil.toJsonStr(req));
        MerchantIdCommand cmd = new MerchantIdCommand();
        cmd.setMerchantId(req.getMerchantId());

        boolean channelConfig = merchantChannelConfigCmdService.syncMerchantChannelConfig(cmd);
        return Mono.just(Result.ok(channelConfig));
    }

}
