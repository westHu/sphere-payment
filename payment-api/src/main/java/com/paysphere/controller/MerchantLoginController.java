package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.MerchantLoginCmdService;
import com.paysphere.command.cmd.MerchantLoginCmd;
import com.paysphere.command.cmd.MerchantPasswordChannelCmd;
import com.paysphere.command.cmd.MerchantPasswordForgetCmd;
import com.paysphere.command.cmd.MerchantPasswordResetCmd;
import com.paysphere.command.cmd.MerchantSetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantShowGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantVerifyGoogleCodeCmd;
import com.paysphere.command.dto.LoginDTO;
import com.paysphere.controller.request.MerchantLoginReq;
import com.paysphere.controller.request.MerchantPasswordChangeReq;
import com.paysphere.controller.request.MerchantPasswordForgetReq;
import com.paysphere.controller.request.MerchantPasswordResetReq;
import com.paysphere.controller.request.MerchantSetGoogleCodeReq;
import com.paysphere.controller.request.MerchantShowGoogleCodeReq;
import com.paysphere.controller.request.MerchantUnsetGoogleCodeReq;
import com.paysphere.controller.request.MerchantVerifyGoogleCodeReq;
import com.paysphere.convert.MerchantLoginConverter;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * 商户登录API
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class MerchantLoginController {

    @Resource
    MerchantLoginCmdService merchantLoginCmdService;
    @Resource
    MerchantLoginConverter merchantLoginConverter;

    /**
     * 商户登录
     */
    @PostMapping("/v1/merchantLogin")
    public Mono<Result<LoginDTO>> merchantLogin(@RequestBody @Validated MerchantLoginReq req) {
        log.info("merchantLogin req={}", JSONUtil.toJsonStr(req));
        MerchantLoginCmd cmd = merchantLoginConverter.convertMerchantLoginCmd(req);

        LoginDTO dto = merchantLoginCmdService.merchantLogin(cmd);
        return Mono.just(Result.ok(dto));
    }

    /**
     * 商户登录-校验谷歌验证码
     */
    @PostMapping("/v1/verifyGoogleCode")
    public Mono<Result<Boolean>> verifyGoogleCode(@RequestBody @Validated MerchantVerifyGoogleCodeReq req) {
        log.info("verifyGoogleCode req={}", JSONUtil.toJsonStr(req));
        MerchantVerifyGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantVerifyGoogleCodeCommand(req);

        boolean verified = merchantLoginCmdService.verifyGoogleCode(cmd);
        return Mono.just(Result.ok(verified));
    }

    /**
     * 商户忘记密码 - 商户平台
     */
    @PostMapping("/v1/forgetPassword")
    public Mono<Result<Boolean>> forgetPassword(@RequestBody @Validated MerchantPasswordForgetReq req) {
        log.info("forgetPassword={}", req.getUsername());
        MerchantPasswordForgetCmd cmd = merchantLoginConverter.convertMerchantPasswordForgetCmd(req);

        boolean forgotten = merchantLoginCmdService.forgetPassword(cmd);
        return Mono.just(Result.ok(forgotten));
    }

    /**
     * 商户修改密码 - 商户平台
     */
    @PostMapping("/v1/changePassword")
    public Mono<Result<Boolean>> changePassword(@RequestBody @Validated MerchantPasswordChangeReq req) {
        log.info("changePassword={}", req.getUsername());
        MerchantPasswordChannelCmd cmd = merchantLoginConverter.convertMerchantPasswordChannelCmd(req);

        boolean changed = merchantLoginCmdService.changePassword(cmd);
        return Mono.just(Result.ok(changed));
    }

    /**
     * 重置商户密码 - 管理平台
     */
    @PostMapping("/v1/resetPassword")
    public Mono<Result<Boolean>> resetPassword(@RequestBody @Validated MerchantPasswordResetReq req) {
        log.info("resetPassword={}", JSONUtil.toJsonStr(req));
        MerchantPasswordResetCmd cmd = merchantLoginConverter.convertMerchantPasswordResetCmd(req);

        boolean reset = merchantLoginCmdService.resetPassword(cmd);
        return Mono.just(Result.ok(reset));
    }

    /**
     * 展示谷歌验证器验证码
     */
    @PostMapping("/v1/showGoogleCode")
    public Mono<Result<String>> showGoogleCode(@RequestBody @Validated MerchantShowGoogleCodeReq req) {
        log.info("showGoogleCode req={}", JSONUtil.toJsonStr(req));
        MerchantShowGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantShowGoogleCodeCmd(req);

        String shown = merchantLoginCmdService.showGoogleAuth(cmd);
        return Mono.just(Result.ok(shown));
    }

    /**
     * 绑定谷歌验证器
     */
    @PostMapping("/v1/setGoogleCode")
    public Mono<Result<Boolean>> setGoogleCode(@RequestBody @Validated MerchantSetGoogleCodeReq req) {
        log.info("setGoogleCode req={}", JSONUtil.toJsonStr(req));
        MerchantSetGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantSetGoogleCodeCmd(req);

        boolean set = merchantLoginCmdService.setGoogleCode(cmd);
        return Mono.just(Result.ok(set));
    }

    /**
     * 解绑谷歌验证器
     */
    @PostMapping("/v1/unsetGoogleCode")
    public Mono<Result<Boolean>> unsetGoogleCode(@RequestBody @Validated MerchantUnsetGoogleCodeReq req) {
        log.info("unsetGoogleCode req={}", JSONUtil.toJsonStr(req));
        MerchantUnsetGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantUnsetGoogleCodeCmd(req);

        boolean unset = merchantLoginCmdService.unsetGoogleAuth(cmd);
        return Mono.just(Result.ok(unset));
    }


}
