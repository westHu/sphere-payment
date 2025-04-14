package api.sphere.controller.api;

import api.sphere.controller.request.CashierReq;
import api.sphere.controller.request.TradeCashierPaymentReq;
import api.sphere.controller.request.TradePaymentReq;
import api.sphere.convert.TradePayConverter;
import app.sphere.command.TradePaymentOrderCmdService;
import app.sphere.command.cmd.TradeCashierPaymentCmd;
import app.sphere.command.cmd.TradePaymentCmd;
import app.sphere.command.dto.TradeCashierPaymentDTO;
import app.sphere.command.dto.TradePaymentDTO;
import app.sphere.query.dto.CashierDTO;
import app.sphere.query.param.CashierParam;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import share.sphere.enums.TradePaymentSourceEnum;
import share.sphere.result.Result;


/**
 * 收款交易API
 */
@Slf4j
@RestController
public class TradeApiPaymentOrderController {

    @Resource
    TradePaymentOrderCmdService tradePaymentOrderCmdService;
    @Resource
    TradePayConverter tradePayConverter;


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
     * Cashier信息 商户渠道配置 & 支付渠道配置
     */
    @PostMapping("/v1/getCashier")
    public Mono<Result<CashierDTO>> getCashier(@RequestBody @Validated CashierReq req) {
        log.info("getCashier req={}", JSONUtil.toJsonStr(req));
        CashierParam param = tradePayConverter.convertCashierParam(req);

        CashierDTO dto = null;//tradePaymentOrderQueryService.getCashier(param);
        return Mono.just(Result.ok(dto));
    }


}
