package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.TradePayoutOrderCmdService;
import com.paysphere.command.cmd.TradeCashCommand;
import com.paysphere.command.cmd.TradeCashRefundCommand;
import com.paysphere.command.cmd.TradeCashSupplementCommand;
import com.paysphere.command.dto.TradePayoutDTO;
import com.paysphere.controller.request.TradeCashOrderPageReq;
import com.paysphere.controller.request.TradeCashRefundReq;
import com.paysphere.controller.request.TradeCashReq;
import com.paysphere.controller.request.TradeCashSupplementReq;
import com.paysphere.controller.request.TradeNoReq;
import com.paysphere.controller.response.TradeCashOrderPageVO;
import com.paysphere.controller.response.TradeCashVO;
import com.paysphere.convert.TradeCashConverter;
import com.paysphere.enums.TradeCashSourceEnum;
import com.paysphere.query.TradeCashOrderQueryService;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.TradeCashOrderDTO;
import com.paysphere.query.dto.TradeCashOrderPageDTO;
import com.paysphere.query.dto.TradeCashReceiptDTO;
import com.paysphere.query.param.TradeCashOrderPageParam;
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
 * 代付交易API
 *
 * @author West.Hu
 * udpate 22.4
 */
@Slf4j
@RestController
public class TradePayoutOrderController {

    @Resource
    TradeCashOrderQueryService tradeCashOrderQueryService;
    @Resource
    TradePayoutOrderCmdService tradePayoutOrderCmdService;
    @Resource
    TradeCashConverter tradeCashConverter;

    /**
     * 代付
     */
    @PostMapping("/v1/payout")
    public Mono<Result<TradeCashVO>> payout(@RequestBody @Validated TradeCashReq req) {
        log.info("payout req={}", JSONUtil.toJsonStr(req));
        TradeCashCommand command = tradeCashConverter.convertTradeCashCommand(req);
        command.setTradeCashSource(TradeCashSourceEnum.API);

        TradePayoutDTO tradePayoutDTO = tradePayoutOrderCmdService.executeCash(command);
        TradeCashVO tradeCashVO = tradeCashConverter.convertTradeCashVO(tradePayoutDTO);
        return Mono.just(Result.ok(tradeCashVO));
    }

    /**
     * 收款补单
     */
    @PostMapping("/v1/cashSupplement")
    public Mono<Result<Boolean>> cashSupplement(@RequestBody @Validated TradeCashSupplementReq req) {
        log.info("cashSupplement req={}", JSONUtil.toJsonStr(req));
        TradeCashSupplementCommand command = tradeCashConverter.convertTradeCashSupplementCommand(req);

        boolean cashSupplement = tradePayoutOrderCmdService.executeCashSupplement(command);
        return Mono.just(Result.ok(cashSupplement));
    }


    /**
     * 收款退单
     */
    @PostMapping("/v1/cashRefund")
    public Mono<Result<Boolean>> cashRefund(@RequestBody @Validated TradeCashRefundReq req) {
        log.info("cashRefund req={}", JSONUtil.toJsonStr(req));
        TradeCashRefundCommand command = tradeCashConverter.convertTradeCashRefundCommand(req);
        boolean cashRefund = tradePayoutOrderCmdService.executeCashRefund(command);
        return Mono.just(Result.ok(cashRefund));
    }


    /**
     * 收款订单列表
     */
    @PostMapping("/v1/pageCashOrderList")
    public Mono<PageResult<TradeCashOrderPageVO>> pageCashOrderList(@RequestBody @Validated TradeCashOrderPageReq req) {
        log.info("pageCashOrderList req={}", JSONUtil.toJsonStr(req));
        TradeCashOrderPageParam param = tradeCashConverter.convertPageParam(req);

        PageDTO<TradeCashOrderPageDTO> pageDTO = tradeCashOrderQueryService.pageCashOrderList(param);
        List<TradeCashOrderPageVO> voList = tradeCashConverter.convertPageVOList(pageDTO.getData());
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), voList));
    }

    /**
     * 导出代付订单列表
     */
    @PostMapping("/v1/exportCashOrderList")
    public Mono<Result<String>> exportCashOrderList(@RequestBody @Validated TradeCashOrderPageReq req) {
        log.info("exportCashOrderList req={}", JSONUtil.toJsonStr(req));
        TradeCashOrderPageParam param = tradeCashConverter.convertPageParam(req);

        String exportCashOrder = tradeCashOrderQueryService.exportCashOrderList(param);
        return Mono.just(Result.ok(exportCashOrder));
    }


    /**
     * 代付订单详情
     */
    @PostMapping("/v1/getCashOrder")
    public Mono<Result<TradeCashOrderDTO>> getCashOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("getCashOrder req={}", JSONUtil.toJsonStr(req));

        TradeCashOrderDTO cashOrderDTO = tradeCashOrderQueryService.getCashOrderByTradeNo(req.getTradeNo());
        return Mono.just(Result.ok(cashOrderDTO));
    }

    /**
     * 代付交易凭证
     */
    @PostMapping("/v1/getCashReceipt")
    public Mono<Result<TradeCashReceiptDTO>> getCashReceipt(@RequestBody @Validated TradeNoReq req) {
        log.info("getCashReceipt req={}", JSONUtil.toJsonStr(req));

        TradeCashReceiptDTO cashReceipt = tradeCashOrderQueryService.getCashReceipt(req.getTradeNo());
        return Mono.just(Result.ok(cashReceipt));
    }

}
