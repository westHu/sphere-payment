package com.paysphere.controller.sandbox;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.SandBoxTradePayOrderCmdService;
import com.paysphere.command.cmd.SandboxTradeForceSuccessCommand;
import com.paysphere.command.cmd.TradeCashierPaymentCmd;
import com.paysphere.command.cmd.TradePaymentCmd;
import com.paysphere.command.dto.TradeCashierPaymentDTO;
import com.paysphere.command.dto.TradePaymentDTO;
import com.paysphere.controller.request.CashierReq;
import com.paysphere.controller.request.SandboxMerchantStepReq;
import com.paysphere.controller.request.SandboxTradeForceSuccessReq;
import com.paysphere.controller.request.SandboxTradePayOrderPageReq;
import com.paysphere.controller.request.TradeCashierPaymentReq;
import com.paysphere.controller.request.TradePaymentReq;
import com.paysphere.controller.response.TradePayVO;
import com.paysphere.convert.SandboxTradeConverter;
import com.paysphere.enums.TradePaymentSourceEnum;
import com.paysphere.query.SandBoxTradeQueryService;
import com.paysphere.query.dto.CashierDTO;
import com.paysphere.query.dto.CashierPaymentMethodDTO;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.SandboxTradePayOrderPageDTO;
import com.paysphere.query.param.CashierParam;
import com.paysphere.query.param.SandboxTradePayOrderPageParam;
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
 * 沙箱 收款交易API
 *
 * @author West.Hu
 */
@Slf4j
@RestController
public class SandBoxTradePayController {

    @Resource
    SandboxTradeConverter sandboxTradeConverter;
    @Resource
    SandBoxTradePayOrderCmdService sandBoxTradePayOrderCmdService;
    @Resource
    SandBoxTradeQueryService sandBoxTradeQueryService;

    /**
     * 沙箱 API收款
     */
    @PostMapping("/sandbox/v1/pay")
    public Mono<Result<TradePayVO>> sandboxPay(@RequestBody @Validated TradePaymentReq req) {
        log.info("sandboxPay req={}", JSONUtil.toJsonStr(req));
        TradePaymentCmd command = sandboxTradeConverter.convertTradePayCommand(req);
        command.setTradePaySource(TradePaymentSourceEnum.API);

        TradePaymentDTO dto = sandBoxTradePayOrderCmdService.executeSandBoxPay(command);
        return Mono.just(Result.ok(sandboxTradeConverter.convertTradePayVO(dto)));
    }


    /**
     * 沙箱 Cashier收款
     */
    @PostMapping("/sandbox/v1/cashierPayment")
    public Mono<Result<TradeCashierPaymentDTO>> sandboxCashierPay(@RequestBody @Validated TradeCashierPaymentReq req) {
        log.info("sandboxCashierPay req={}", JSONUtil.toJsonStr(req));
        TradeCashierPaymentCmd command = sandboxTradeConverter.convertTradeCashierPayCommand(req);

        TradeCashierPaymentDTO cashierPayDTO = sandBoxTradePayOrderCmdService.executeSandboxCashierPay(command);
        return Mono.just(Result.ok(cashierPayDTO));
    }


    /**
     * 沙箱 Cashier信息 商户渠道配置 & 支付渠道配置
     */
    @PostMapping("/sandbox/v1/getCashier")
    public Mono<Result<CashierDTO>> getSandboxCashier(@RequestBody @Validated CashierReq req) {
        log.info("getSandboxCashier req={}", JSONUtil.toJsonStr(req));
        CashierParam param = sandboxTradeConverter.convertCashierParam(req);

        CashierDTO sandboxCashier = sandBoxTradeQueryService.getSandboxCashier(param);
        return Mono.just(Result.ok(sandboxCashier));
    }


    /**
     * 沙箱 API收款-强制成功/失败
     */
    @PostMapping("/sandbox/v1/payForceSuccessOrFailed")
    public Mono<Result<Boolean>> sandboxPayForceSuccessOrFailed(@RequestBody @Validated
                                                                    SandboxTradeForceSuccessReq req) {
        log.info("sandboxPayForceSuccessOrFailed req={}", JSONUtil.toJsonStr(req));
        SandboxTradeForceSuccessCommand command = sandboxTradeConverter.convertSandboxTradeForceSuccessCommand(req);

        boolean successOrFailed = sandBoxTradePayOrderCmdService.sandboxPayForceSuccessOrFailed(command);
        return Mono.just(Result.ok(successOrFailed));
    }


    /**
     * 沙箱 获取沙箱对接步骤
     */
    @PostMapping("/sandbox/v1/getMerchantStep")
    public Mono<Result<String>> getSandboxMerchantStep(@RequestBody @Validated SandboxMerchantStepReq req) {
        log.info("getSandboxMerchantStep req={}", JSONUtil.toJsonStr(req));
        String merchantId = req.getMerchantId();

        String merchantStep = sandBoxTradeQueryService.getSandboxMerchantStep(merchantId);
        return Mono.just(Result.ok(merchantStep));
    }

    /**
     * 沙箱 分页查询沙箱收款订单列表
     */
    @PostMapping("/sandbox/v1/pagePayOrderList")
    public Mono<PageResult<SandboxTradePayOrderPageDTO>> pageSandboxPayOrderList(@RequestBody @Validated
                                                                                     SandboxTradePayOrderPageReq req) {
        log.info("pageSandboxPayOrderList req={}", JSONUtil.toJsonStr(req));
        SandboxTradePayOrderPageParam param = sandboxTradeConverter.convertSandboxTradePayOrderPageParam(req);

        PageDTO<SandboxTradePayOrderPageDTO> pageDTO = sandBoxTradeQueryService.pageSandBoxPayOrderList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }


    /**
     * 沙箱 查询收银台支付方式
     */
    @PostMapping("/sandbox/v1/getPaymentMethodList4Cashier")
    public Mono<Result<List<CashierPaymentMethodDTO>>> getSandboxPaymentMethodList4Cashier() {
        log.info("getSandboxPaymentMethodList4Cashier");
        List<CashierPaymentMethodDTO> cashier = sandBoxTradeQueryService.getSandboxPaymentMethodList4Cashier();
        return Mono.just(Result.ok(cashier));
    }

}
