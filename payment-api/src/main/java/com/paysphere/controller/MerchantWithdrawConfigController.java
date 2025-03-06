package com.paysphere.controller;


import cn.hutool.json.JSONUtil;
import com.paysphere.command.MerchantWithdrawConfigCmdService;
import com.paysphere.command.cmd.MerchantWithdrawCommand;
import com.paysphere.command.cmd.MerchantWithdrawConfigUpdateCommand;
import com.paysphere.controller.request.MerchantIdReq;
import com.paysphere.controller.request.MerchantWithdrawConfigUpdateReq;
import com.paysphere.controller.request.MerchantWithdrawReq;
import com.paysphere.convert.MerchantWithdrawConverter;
import com.paysphere.query.MerchantWithdrawConfigQueryService;
import com.paysphere.query.dto.MerchantWithdrawConfigDTO;
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
public class MerchantWithdrawConfigController {

    @Resource
    MerchantWithdrawConverter merchantWithdrawConverter;
    @Resource
    MerchantWithdrawConfigQueryService merchantWithdrawConfigQueryService;
    @Resource
    MerchantWithdrawConfigCmdService merchantWithdrawConfigCmdService;

    /**
     * 查询商户提现配置
     */
    @PostMapping("/v1/getMerchantWithdrawConfig")
    public Mono<Result<MerchantWithdrawConfigDTO>> getMerchantWithdrawConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("getMerchantWithdrawConfig req={}", JSONUtil.toJsonStr(req));
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());

        MerchantWithdrawConfigDTO withdrawConfig = merchantWithdrawConfigQueryService.getMerchantWithdrawConfig(param);
        return Mono.just(Result.ok(withdrawConfig));
    }

    /**
     * 更新商户提现配置
     */
    @PostMapping("/v1/updateMerchantWithdrawConfig")
    public Mono<Result<Boolean>> updateMerchantWithdrawConfig(@RequestBody @Validated MerchantWithdrawConfigUpdateReq req) {
        log.info("updateMerchantWithdrawConfig req={}", JSONUtil.toJsonStr(req));
        MerchantWithdrawConfigUpdateCommand command =
                merchantWithdrawConverter.convertMerchantWithdrawConfigUpdateCommand(req);

        boolean update = merchantWithdrawConfigCmdService.updateMerchantWithdrawConfig(command);
        return Mono.just(Result.ok(update));
    }

    /**
     * 提现 此处提现可提现到银行\代付账户
     */
    @PostMapping("/v1/withdraw")
    public Mono<Result<Boolean>> withdraw(@RequestBody @Validated MerchantWithdrawReq req) {
        log.info("withdraw req={}", JSONUtil.toJsonStr(req));
        MerchantWithdrawCommand command = merchantWithdrawConverter.convertMerchantWithdrawCommand(req);

        boolean withdraw = merchantWithdrawConfigCmdService.withdraw(command);
        return Mono.just(Result.ok(withdraw));
    }

}
