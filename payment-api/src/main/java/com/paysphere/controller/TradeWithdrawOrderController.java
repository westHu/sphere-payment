package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.command.TradeWithdrawOrderCmdService;
import com.paysphere.command.cmd.TradeWithdrawCommand;
import com.paysphere.controller.request.TradeNoReq;
import com.paysphere.controller.request.TradeWithdrawOrderPageReq;
import com.paysphere.controller.request.TradeWithdrawReq;
import com.paysphere.controller.request.WithdrawFlagReq;
import com.paysphere.controller.response.TradeWithdrawOrderPageVO;
import com.paysphere.convert.TradeWithdrawConverter;
import com.paysphere.db.entity.TradeWithdrawOrder;
import com.paysphere.query.TradeWithdrawOrderQueryService;
import com.paysphere.query.dto.TradeWithdrawOrderDTO;
import com.paysphere.query.param.TradeWithdrawOrderPageParam;
import com.paysphere.query.param.WithdrawFlagParam;
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
 * 提现打款交易API
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class TradeWithdrawOrderController {

    @Resource
    TradeWithdrawOrderCmdService tradeWithdrawOrderCmdService;
    @Resource
    TradeWithdrawOrderQueryService tradeWithdrawOrderQueryService;
    @Resource
    TradeWithdrawConverter tradeWithdrawConverter;


    /**
     * 提现打款(线下打款)
     */
    @PostMapping("/v1/withdraw")
    public Mono<Result<Boolean>> withdraw(@RequestBody @Validated TradeWithdrawReq req) {
        log.info("withdraw req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawCommand command = tradeWithdrawConverter.convertTradeWithdrawCommand(req);

        boolean executeWithdraw = tradeWithdrawOrderCmdService.executeWithdraw(command);
        return Mono.just(Result.ok(executeWithdraw));
    }

    /**
     * 分页查询提现订单
     */
    @PostMapping("/v1/pageWithdrawOrderList")
    public Mono<PageResult<TradeWithdrawOrderPageVO>> pageWithdrawOrderList(@RequestBody @Validated
                                                                            TradeWithdrawOrderPageReq req) {
        log.info("pageWithdrawOrderList req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawOrderPageParam param = tradeWithdrawConverter.convertTradeWithdrawOrderPageParam(req);

        Page<TradeWithdrawOrder> page = tradeWithdrawOrderQueryService.pageWithdrawOrderList(param);
        List<TradeWithdrawOrderPageVO> voList = tradeWithdrawConverter.convertTradeWithdrawOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出提现订单
     */
    @PostMapping("/v1/exportWithdrawOrderList")
    public Mono<Result<String>> exportWithdrawOrderList(@RequestBody @Validated TradeWithdrawOrderPageReq req) {
        log.info("exportWithdrawOrderList req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawOrderPageParam param = tradeWithdrawConverter.convertTradeWithdrawOrderPageParam(req);

        String exportWithdrawOrder = tradeWithdrawOrderQueryService.exportWithdrawOrderList(param);
        return Mono.just(Result.ok(exportWithdrawOrder));
    }


    /**
     * 查询提现订单
     */
    @PostMapping("/v1/getWithdrawOrder")
    public Mono<Result<TradeWithdrawOrderDTO>> getWithdrawOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("getWithdrawOrder req={}", JSONUtil.toJsonStr(req));

        TradeWithdrawOrderDTO withdrawOrderDTO = tradeWithdrawOrderQueryService.getWithdrawOrder(req.getTradeNo());
        return Mono.just(Result.ok(withdrawOrderDTO));
    }

    /**
     * 是否自动提现或者转账标签
     */
    @PostMapping("/v1/getWithdrawFlag")
    public Mono<Result<Boolean>> getWithdrawFlag(@RequestBody @Validated WithdrawFlagReq req) {
        log.info("getWithdrawOrder req={}", JSONUtil.toJsonStr(req));
        WithdrawFlagParam param = tradeWithdrawConverter.convertWithdrawFlagParam(req);

        boolean withdrawFlag = tradeWithdrawOrderQueryService.getWithdrawFlag(param);
        return Mono.just(Result.ok(withdrawFlag));
    }
}
