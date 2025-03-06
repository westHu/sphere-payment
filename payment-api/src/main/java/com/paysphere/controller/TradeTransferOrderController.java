package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.command.TradeTransferOrderCmdService;
import com.paysphere.command.cmd.TradeTransferCommand;
import com.paysphere.controller.request.TradeNoReq;
import com.paysphere.controller.request.TradeTransferOrderPageReq;
import com.paysphere.controller.request.TradeTransferReq;
import com.paysphere.controller.response.TradeTransferOrderPageVO;
import com.paysphere.convert.TradeTransferConverter;
import com.paysphere.db.entity.TradeTransferOrder;
import com.paysphere.enums.TradeStatusEnum;
import com.paysphere.query.TradeTransferOrderQueryService;
import com.paysphere.query.dto.TradeTransferOrderDTO;
import com.paysphere.query.param.TradeTransferOrderPageParam;
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
 * 转账API
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class TradeTransferOrderController {

    @Resource
    TradeTransferConverter tradeTransferConverter;
    @Resource
    TradeTransferOrderCmdService tradeTransferOrderCmdService;
    @Resource
    TradeTransferOrderQueryService tradeTransferOrderQueryService;

    /**
     * API转账
     */
    @PostMapping("/v1/transfer")
    public Mono<Result<Boolean>> transfer(@RequestBody @Validated TradeTransferReq req) {
        log.info("transfer req={}", JSONUtil.toJsonStr(req));
        TradeTransferCommand command = tradeTransferConverter.convertTradeTransferCommand(req);

        boolean executeTransfer = tradeTransferOrderCmdService.executeTransfer(command);
        return Mono.just(Result.ok(executeTransfer));
    }


    /**
     * 分页查询转账订单列表
     */
    @PostMapping("/v1/pageTransferOrderList")
    public Mono<PageResult<TradeTransferOrderPageVO>> pageTransferOrderList(@RequestBody @Validated
                                                                            TradeTransferOrderPageReq req) {
        log.info("pageTransferOrderList req={}", JSONUtil.toJsonStr(req));
        TradeTransferOrderPageParam param = tradeTransferConverter.convertTradeTransferOrderPageParam(req);

        Page<TradeTransferOrder> page = tradeTransferOrderQueryService.pageTransferOrderList(param);

        // 处理失败的结束时间
        page.getRecords().forEach(e -> {
            if (e.getTradeStatus().equals(TradeStatusEnum.TRADE_FAILED.getCode())) {
//                e.setSettleFinishTime(e.getUpdateTime());
            }
        });
        List<TradeTransferOrderPageVO> voList =
                tradeTransferConverter.convertTradeTransferOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出转账订单列表
     */
    @PostMapping("/v1/exportTransferOrderList")
    public Mono<Result<String>> exportTransferOrderList(@RequestBody @Validated TradeTransferOrderPageReq req) {
        log.info("tradePay exportTransferOrderList req={}", JSONUtil.toJsonStr(req));
        TradeTransferOrderPageParam param = tradeTransferConverter.convertTradeTransferOrderPageParam(req);

        String exportTransferOrder = tradeTransferOrderQueryService.exportTransferOrderList(param);
        return Mono.just(Result.ok(exportTransferOrder));
    }


    /**
     * 查询转账订单详情
     */
    @PostMapping("/v1/getTransferOrder")
    public Mono<Result<TradeTransferOrderDTO>> getTransferOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("tradePay getTransferOrder req={}", JSONUtil.toJsonStr(req));

        TradeTransferOrderDTO dto = tradeTransferOrderQueryService.getTransferOrderByTradeNo(req.getTradeNo());
        return Mono.just(Result.ok(dto));
    }
}
