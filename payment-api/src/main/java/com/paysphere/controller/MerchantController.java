package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.MerchantApiCmdService;
import com.paysphere.command.cmd.MerchantUpdateStatusCommand;
import com.paysphere.controller.request.MerchantSendEmailCodeReq;
import com.paysphere.controller.request.MerchantUpdateStatusReq;
import com.paysphere.controller.request.MerchantVerifyEmailCodeReq;
import com.paysphere.convert.MerchantApiConverter;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 基本商户Api
 */
@Slf4j
@RestController
public class MerchantController {

    @Resource
    MerchantApiCmdService merchantApiCmdService;
    @Resource
    MerchantApiConverter merchantApiConverter;


    /**
     * 通过邮件发送验证码
     */
    @PostMapping("/v1/sendCode2Email")
    public Mono<Result<Boolean>> sendCode2Email(@RequestBody @Validated MerchantSendEmailCodeReq req) {
        log.info("sendCode req={}", JSONUtil.toJsonStr(req));

        boolean sent = merchantApiCmdService.sendCode2Email(req.getEmail());
        return Mono.just(Result.ok(sent));
    }

    /**
     * 通过邮件验证验证码
     */
    @PostMapping("/v1/verifyCode4Email")
    public Mono<Result<Boolean>> verifyCode4Email(@RequestBody @Validated MerchantVerifyEmailCodeReq req) {
        log.info("verifyCode4Email req={}", JSONUtil.toJsonStr(req));

        boolean verified = merchantApiCmdService.verifyCode4Email(req.getEmail(), req.getCode());
        return Mono.just(Result.ok(verified));
    }

    /**
     * 更新状态, 冻结、注销、正常；休眠走定时任务
     */
    @PostMapping("/v1/updateMerchantStatus")
    public Mono<Result<Boolean>> updateMerchantStatus(@RequestBody @Validated MerchantUpdateStatusReq req) {
        log.info("UpdateMerchantStatus req={}", JSONUtil.toJsonStr(req));
        MerchantUpdateStatusCommand command = merchantApiConverter.convertMerchantUpdateStatusCommand(req);

        boolean updated = merchantApiCmdService.updateMerchantStatus(command);
        return Mono.just(Result.ok(updated));
    }

}
