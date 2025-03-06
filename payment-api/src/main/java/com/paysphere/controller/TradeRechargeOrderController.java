package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.command.TradeRechargeOrderCmdService;
import com.paysphere.command.cmd.TradePreRechargeCommand;
import com.paysphere.command.cmd.TradeRechargeCommand;
import com.paysphere.command.dto.PreRechargeDTO;
import com.paysphere.controller.request.TradePreRechargeReq;
import com.paysphere.controller.request.TradeRechargeOrderPageReq;
import com.paysphere.controller.request.TradeRechargeReq;
import com.paysphere.controller.response.TradeRechargeOrderPageVO;
import com.paysphere.convert.TradeRechargeConverter;
import com.paysphere.db.entity.TradeRechargeOrder;
import com.paysphere.query.TradeRechargeOrderQueryService;
import com.paysphere.query.param.TradeRechargeOrderPageParam;
import com.paysphere.result.PageResult;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;



/**
 * 充值交易API
 *
 * @author West.Hu
 * update h 22.4
 */
@Slf4j
@RestController
public class TradeRechargeOrderController {

    @Resource
    TradeRechargeOrderCmdService tradeRechargeOrderCmdService;
    @Resource
    TradeRechargeOrderQueryService tradeRechargeOrderQueryService;
    @Resource
    TradeRechargeConverter tradeRechargeConverter;


    /**
     * 商户平台充值前置
     */
    @PostMapping("/v1/preRecharge")
    public Mono<Result<PreRechargeDTO>> preRecharge(@RequestBody @Validated TradePreRechargeReq req) {
        log.info("preRecharge req={}", JSONUtil.toJsonStr(req));
        TradePreRechargeCommand command = tradeRechargeConverter.convertTradePreRechargeCommand(req);

        PreRechargeDTO preRechargeDTO = tradeRechargeOrderCmdService.executePreRecharge(command);
        return Mono.just(Result.ok(preRechargeDTO));
    }

    /**
     * 商户平台充值
     */
    @PostMapping("/v1/recharge")
    public Mono<Result<Boolean>> recharge(@RequestBody @Validated TradeRechargeReq req) {
        log.info("recharge req={}", JSONUtil.toJsonStr(req));
        TradeRechargeCommand command = tradeRechargeConverter.convertTradeRechargeCommand(req);

        boolean executeRecharge = tradeRechargeOrderCmdService.executeRecharge(command);
        return Mono.just(Result.ok(executeRecharge));
    }

    /**
     * 分页查询充值订单
     */
    @PostMapping("/v1/pageRechargeOrderList")
    public Mono<PageResult<TradeRechargeOrderPageVO>> pageRechargeOrderList(@RequestBody @Validated
                                                                            TradeRechargeOrderPageReq req) {
        log.info("pageRechargeOrderList req={}", JSONUtil.toJsonStr(req));
        TradeRechargeOrderPageParam param = tradeRechargeConverter.convertTradeRechargeOrderPageParam(req);

        Page<TradeRechargeOrder> page = null;//tradeRechargeOrderQueryService.pageRechargeOrderList(param);
        List<TradeRechargeOrderPageVO> voList = tradeRechargeConverter.convertTradeRechargeOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出充值订单
     */
    @PostMapping("/v1/exportRechargeOrderList")
    public Mono<Result<String>> exportRechargeOrderList(@RequestBody @Validated TradeRechargeOrderPageReq req) {
        log.info("exportRechargeOrderList req={}", JSONUtil.toJsonStr(req));
        TradeRechargeOrderPageParam param = tradeRechargeConverter.convertTradeRechargeOrderPageParam(req);

        String exportRechargeOrder = null;//tradeRechargeOrderQueryService.exportRechargeOrderList(param);
        return Mono.just(Result.ok(exportRechargeOrder));
    }

    /**
     * 再次审核(INIT\FAILED)
     */
    @PostMapping("/v1/reviewRecharge")
    public Mono<Result<Boolean>> reviewRecharge(@RequestBody @Validated TradeRechargeReq req) {
        log.info("reviewRecharge req={}", JSONUtil.toJsonStr(req));
        String tradeNo = req.getTradeNo();

        boolean reviewRecharge = tradeRechargeOrderCmdService.reviewRecharge(tradeNo);
        return Mono.just(Result.ok(reviewRecharge));
    }

}
