package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.TradeReviewCmdService;
import com.paysphere.command.cmd.TradeReviewCommand;
import com.paysphere.controller.request.TradeReviewReq;
import com.paysphere.convert.TradeReviewConverter;
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
public class TradeReviewController {

    @Resource
    TradeReviewCmdService tradeReviewCmdService;
    @Resource
    TradeReviewConverter tradeReviewConverter;

    /**
     * 交易订单审核[代付、转账...][通过/拒绝]
     */
    @PostMapping("/v1/tradeReview")
    public Mono<Result<Boolean>> tradeReview(@RequestBody @Validated TradeReviewReq req) {
        log.info("tradeReview req={}", JSONUtil.toJsonStr(req));
        TradeReviewCommand command = tradeReviewConverter.convertTradeReviewCommand(req);

        boolean tradeReview = tradeReviewCmdService.executeTradeReview(command);
        return Mono.just(Result.ok(tradeReview));
    }

}
