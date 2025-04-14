package api.sphere.controller.front.merchant.sandbox;

import api.sphere.controller.request.CashierReq;
import api.sphere.controller.request.SandboxMerchantStepReq;
import api.sphere.controller.request.SandboxTradeForceSuccessReq;
import api.sphere.controller.request.SandboxTradePayOrderPageReq;
import api.sphere.controller.request.TradeCashierPaymentReq;
import api.sphere.controller.request.TradePaymentReq;
import api.sphere.controller.response.TradePayVO;
import api.sphere.convert.SandboxTradeConverter;
import app.sphere.command.SandBoxTradePayOrderCmdService;
import app.sphere.command.cmd.SandboxTradeForceSuccessCommand;
import app.sphere.command.cmd.TradeCashierPaymentCmd;
import app.sphere.command.cmd.TradePaymentCmd;
import app.sphere.command.dto.TradeCashierPaymentDTO;
import app.sphere.command.dto.TradePaymentDTO;
import app.sphere.query.SandBoxTradeQueryService;
import app.sphere.query.dto.CashierDTO;
import app.sphere.query.dto.CashierPaymentMethodDTO;
import app.sphere.query.dto.PageDTO;
import app.sphere.query.dto.SandboxTradePaymentOrderPageDTO;
import app.sphere.query.param.CashierParam;
import app.sphere.query.param.SandboxTradePaymentOrderPageParam;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import share.sphere.enums.TradePaymentSourceEnum;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

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
    @PostMapping("/sandbox/v1/pagePaymentOrderList")
    public Mono<PageResult<SandboxTradePaymentOrderPageDTO>> pageSandboxPayOrderList(@RequestBody @Validated
                                                                                     SandboxTradePayOrderPageReq req) {
        log.info("pageSandboxPayOrderList req={}", JSONUtil.toJsonStr(req));
        SandboxTradePaymentOrderPageParam param = sandboxTradeConverter.convertSandboxTradePayOrderPageParam(req);

        PageDTO<SandboxTradePaymentOrderPageDTO> pageDTO = sandBoxTradeQueryService.pageSandBoxPayOrderList(param);
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
