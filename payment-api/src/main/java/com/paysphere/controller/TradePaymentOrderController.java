package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.paysphere.command.TradePaymentOrderCmdService;
import com.paysphere.command.cmd.TradeCashierPaymentCmd;
import com.paysphere.command.cmd.TradePaymentCmd;
import com.paysphere.command.cmd.TradePaymentLinkCmd;
import com.paysphere.command.cmd.TradePaymentRefundCmd;
import com.paysphere.command.cmd.TradePaymentSupplementCmd;
import com.paysphere.command.dto.TradeCashierPaymentDTO;
import com.paysphere.command.dto.TradePaymentDTO;
import com.paysphere.controller.request.CashierReq;
import com.paysphere.controller.request.TradeCashierPaymentReq;
import com.paysphere.controller.request.TradeNoReq;
import com.paysphere.controller.request.TradePayOrderPageReq;
import com.paysphere.controller.request.TradePaymentLinkPageReq;
import com.paysphere.controller.request.TradePaymentLinkReq;
import com.paysphere.controller.request.TradePaymentRefundReq;
import com.paysphere.controller.request.TradePaymentReq;
import com.paysphere.controller.request.TradePaymentSupplementReq;
import com.paysphere.controller.response.TradePaymentLinkOrderVO;
import com.paysphere.convert.TradePayConverter;
import com.paysphere.db.entity.TradePaymentLinkOrder;
import com.paysphere.enums.TradePaymentSourceEnum;
import com.paysphere.query.TradePaymentOrderQueryService;
import com.paysphere.query.dto.CashierDTO;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.TradePayOrderDTO;
import com.paysphere.query.dto.TradePayOrderPageDTO;
import com.paysphere.query.param.CashierParam;
import com.paysphere.query.param.TradePayOrderPageParam;
import com.paysphere.query.param.TradePaymentLinkPageParam;
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
 * 收款交易API
 */
@Slf4j
@RestController
public class TradePaymentOrderController {

    @Resource
    TradePaymentOrderCmdService tradePaymentOrderCmdService;
    @Resource
    TradePaymentOrderQueryService tradePaymentOrderQueryService;
    @Resource
    TradePayConverter tradePayConverter;


    /**
     * PaymentLink收款
     */
    @PostMapping("/v1/createPaymentLink")
    public Mono<Result<String>> createPaymentLink(@RequestBody @Validated TradePaymentLinkReq req) {
        log.info("createPaymentLink req={}", JSONUtil.toJsonStr(req));
        TradePaymentLinkCmd cmd = tradePayConverter.convertTradePaymentLinkCmd(req);

        String paymentLink = tradePaymentOrderCmdService.executePaymentLink(cmd);
        return Mono.just(Result.ok(paymentLink));
    }

    /**
     * API收款
     */
    @PostMapping("/v1/apiPayment")
    public Mono<Result<TradePaymentDTO>> apiPayment(@RequestBody @Validated TradePaymentReq req) {
        log.info("apiPayment req={}", JSONUtil.toJsonStr(req));
        TradePaymentCmd cmd = tradePayConverter.convertTradePayCmd(req);
        cmd.setTradePaySource(TradePaymentSourceEnum.API);

        TradePaymentDTO paymentDTO = tradePaymentOrderCmdService.executeApiPayment(cmd);
        return Mono.just(Result.ok(paymentDTO));
    }

    /**
     * 收银台收款
     */
    @PostMapping("/v1/cashierPayment")
    public Mono<Result<TradeCashierPaymentDTO>> cashierPayment(@RequestBody @Validated TradeCashierPaymentReq req) {
        log.info("cashierPayment req={}", JSONUtil.toJsonStr(req));
        TradeCashierPaymentCmd cmd = tradePayConverter.convertTradeCashierPaymentCmd(req);

        TradeCashierPaymentDTO paymentDTO = tradePaymentOrderCmdService.executeCashierPay(cmd);
        return Mono.just(Result.ok(paymentDTO));
    }

    /**
     * 收款补单
     */
    @PostMapping("/v1/paymentSupplement")
    public Mono<Result<Boolean>> paymentSupplement(@RequestBody @Validated TradePaymentSupplementReq req) {
        log.info("paymentSupplement req={}", JSONUtil.toJsonStr(req));
        TradePaymentSupplementCmd cmd = tradePayConverter.convertTradePaySupplementCmd(req);

        boolean supplement = tradePaymentOrderCmdService.executePaymentSupplement(cmd);
        return Mono.just(Result.ok(supplement));
    }

    /**
     * 收款退单
     */
    @PostMapping("/v1/paymentRefund")
    public Mono<Result<Boolean>> paymentRefund(@RequestBody @Validated TradePaymentRefundReq req) {
        log.info("paymentRefund req={}", JSONUtil.toJsonStr(req));
        TradePaymentRefundCmd cmd = tradePayConverter.convertTradePaymentRefundCmd(req);

        boolean refund = tradePaymentOrderCmdService.executePaymentRefund(cmd);
        return Mono.just(Result.ok(refund));
    }

    /**
     * Cashier信息 商户渠道配置 & 支付渠道配置
     */
    @PostMapping("/v1/getCashier")
    public Mono<Result<CashierDTO>> getCashier(@RequestBody @Validated CashierReq req) {
        log.info("getCashier req={}", JSONUtil.toJsonStr(req));
        CashierParam param = tradePayConverter.convertCashierParam(req);

        CashierDTO dto = null;//tradePaymentOrderQueryService.getCashier(param);
        return Mono.just(Result.ok(dto));
    }

    /**
     * 分页查询PaymentLink收款
     */
    @PostMapping("/v1/pagePaymentLinkList")
    public Mono<PageResult<TradePaymentLinkOrderVO>> pagePaymentLinkList(@RequestBody @Validated
                                                                         TradePaymentLinkPageReq req) {
        log.info("paymentLink req={}", JSONUtil.toJsonStr(req));
        TradePaymentLinkPageParam param = tradePayConverter.convertTradePaymentLinkPageParam(req);
        Page<TradePaymentLinkOrder> page = null;//tradePayOrderQueryService.pagePaymentLinkList(param);
        List<TradePaymentLinkOrderVO> voList = tradePayConverter.convertTradePaymentLinkOrderVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 分页查询收款订单列表
     */
    @PostMapping("/v1/pagePayOrderList")
    public Mono<PageResult<TradePayOrderPageDTO>> pagePayOrderList(@RequestBody @Validated TradePayOrderPageReq req) {
        log.info("PagePayOrderList req={}", JSONUtil.toJsonStr(req));
        TradePayOrderPageParam param = tradePayConverter.convertPageParam(req);

        PageDTO<TradePayOrderPageDTO> pageDTO = null;//tradePayOrderQueryService.pagePayOrderList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 导出收款订单列表
     */
    @PostMapping("/v1/exportPayOrderList")
    public Mono<Result<String>> exportPayOrderList(@RequestBody @Validated TradePayOrderPageReq req) {
        log.info("exportPayOrderList req={}", JSONUtil.toJsonStr(req));
        TradePayOrderPageParam param = tradePayConverter.convertPageParam(req);

        String exportPayOrder = null;//tradePayOrderQueryService.exportPayOrderList(param);
        return Mono.just(Result.ok(exportPayOrder));
    }

    /**
     * 收款订单详情
     */
    @PostMapping("/v1/getPayOrder")
    public Mono<Result<TradePayOrderDTO>> getPayOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("getPayOrder req={}", JSONUtil.toJsonStr(req));
        TradePayOrderDTO dto = null;//tradePayOrderQueryService.getPayOrderByTradeNo(req.getTradeNo());
        return Mono.just(Result.ok(dto));
    }

}
