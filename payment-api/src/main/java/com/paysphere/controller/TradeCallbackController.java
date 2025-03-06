package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.TradeCallBackCmdService;
import com.paysphere.command.cmd.TradeCallbackCmd;
import com.paysphere.controller.request.TradeCallbackReq;
import com.paysphere.convert.TradeCallbackConverter;
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
public class TradeCallbackController {

    @Resource
    TradeCallbackConverter tradeCallbackConverter;
    @Resource
    TradeCallBackCmdService tradeCallBackCmdService;

    /**
     * 订单补发回调通知
     */
    @PostMapping("/v1/callback")
    public Mono<Result<Boolean>> tradeCallback(@RequestBody @Validated TradeCallbackReq req) {
        log.info("tradeCallback req={}", JSONUtil.toJsonStr(req));
        TradeCallbackCmd cmd = tradeCallbackConverter.convertTradeCallbackCmd(req);

        return Mono.just(Result.ok(tradeCallBackCmdService.handlerTradeCallback(cmd)));
    }
}
