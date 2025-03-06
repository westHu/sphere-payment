package com.paysphere.controller;


import cn.hutool.json.JSONUtil;
import com.paysphere.command.SettleRefundCmdService;
import com.paysphere.command.cmd.SettleRefundCmd;
import com.paysphere.command.cmd.SettleSupplementCmd;
import com.paysphere.controller.request.SettleRefundReq;
import com.paysphere.controller.request.SettleSupplementReq;
import com.paysphere.convert.SettleRefundConverter;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
public class SettleRefundController {

    @Resource
    SettleRefundConverter settleRefundConverter;
    @Resource
    SettleRefundCmdService settleRefundCmdService;

    /**
     * 同步补单-收款、代付使用 *重要*
     */
    @PostMapping("/v1/supplement")
    public Mono<Result<Boolean>> supplement(@RequestBody @Validated SettleSupplementReq req) {
        log.info("supplement for pay|cash req={}", JSONUtil.toJsonStr(req));
        SettleSupplementCmd cmd = settleRefundConverter.convertSupplementCmd(req);

        boolean supplement = settleRefundCmdService.handlerSupplement(cmd);
        return Mono.just(Result.ok(supplement));
    }


    /**
     * 同步退单-收款、代付使用 *重要*
     */
    @PostMapping("/v1/refund")
    public Mono<Result<Boolean>> refund(@RequestBody @Validated SettleRefundReq req) {
        log.info("refund for pay|cash req={}", JSONUtil.toJsonStr(req));
        SettleRefundCmd cmd = settleRefundConverter.convertRefundCmd(req);

        boolean refund = settleRefundCmdService.handlerRefund(cmd);
        return Mono.just(Result.ok(refund));
    }


}
