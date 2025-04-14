package api.sphere.controller.front.merchant;

import api.sphere.controller.request.*;
import api.sphere.controller.response.TradePayVO;
import api.sphere.controller.response.TradePayoutVO;
import api.sphere.convert.SandboxTradeConverter;
import api.sphere.convert.TradeHistoryConverter;
import app.sphere.command.SandBoxTradeCashOrderCmdService;
import app.sphere.command.SandBoxTradePayOrderCmdService;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.*;
import app.sphere.query.SandBoxTradeQueryService;
import app.sphere.query.SandboxTradeHistoryService;
import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import share.sphere.enums.TradePaymentSourceEnum;
import share.sphere.enums.TradePayoutSourceEnum;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

import java.util.List;

/**
 * 商户沙箱管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/sandbox/merchant/admin")
public class MerchantSandBoxAdminController {

    // ============== 依赖注入 ==============
    @Resource
    SandboxTradeConverter sandboxTradeConverter;
    @Resource
    TradeHistoryConverter tradeHistoryConverter;
    @Resource
    SandBoxTradeQueryService sandBoxTradeQueryService;
    @Resource
    SandBoxTradeCashOrderCmdService sandBoxTradeCashOrderCmdService;
    @Resource
    SandBoxTradePayOrderCmdService sandBoxTradePayOrderCmdService;
    @Resource
    SandboxTradeHistoryService sandboxTradeHistoryService;

    // ============== 代付相关接口 ==============
    /**
     * 沙箱 API代付
     */
    @PostMapping("/sandbox/v1/cash")
    public Mono<Result<TradePayoutVO>> sandboxCash(@RequestBody @Validated TradePayoutReq req) {
        log.info("sandboxCash req={}", JSONUtil.toJsonStr(req));
        TradePayoutCommand command = sandboxTradeConverter.convertTradePayoutCommand(req);
        command.setTradePayoutSourceEnum(TradePayoutSourceEnum.API);

        TradePayoutDTO dto = sandBoxTradeCashOrderCmdService.executeSandboxCash(command);
        return Mono.just(Result.ok(sandboxTradeConverter.convertTradeCashVO(dto)));
    }

    /**
     * 沙箱 API代付-强制成功/失败
     */
    @PostMapping("/sandbox/v1/cashForceSuccessOrFailed")
    public Mono<Result<Boolean>> sandboxCashForceSuccessOrFailed(@RequestBody @Validated SandboxTradeForceSuccessReq req) {
        log.info("cashForceSuccessOrFailed req={}", JSONUtil.toJsonStr(req));
        SandboxTradeForceSuccessCommand command = sandboxTradeConverter.convertSandboxTradeForceSuccessCommand(req);

        boolean successOrFailed = sandBoxTradeCashOrderCmdService.sandboxCashForceSuccessOrFailed(command);
        return Mono.just(Result.ok(successOrFailed));
    }

    /**
     * 沙箱 分页查询沙箱代付单列表
     */
    @PostMapping("/sandbox/v1/pagePayoutOrderList")
    public Mono<PageResult<SandboxTradePayoutOrderPageDTO>> pageSandboxCashOrderList(@RequestBody @Validated SandboxTradeCashOrderPageReq req) {
        log.info("pageSandboxCashOrderList req={}", JSONUtil.toJsonStr(req));
        SandboxTradePayoutOrderPageParam param = sandboxTradeConverter.convertSandboxTradeCashOrderPageParam(req);

        PageDTO<SandboxTradePayoutOrderPageDTO> pageDTO = sandBoxTradeQueryService.pageSandboxCashOrderList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    // ============== 收款相关接口 ==============
    /**
     * 沙箱 PaymentLink收款
     */
    @PostMapping("/sandbox/v1/createPaymentLink")
    public Mono<Result<String>> createSandboxPaymentLink(@RequestBody @Validated TradePaymentReq req) {
        log.info("createSandboxPaymentLink req={}", JSONUtil.toJsonStr(req));
        TradePaymentCmd command = sandboxTradeConverter.convertTradePayCommand(req);
        command.setTradePaySource(TradePaymentSourceEnum.PAY_LINK);

        TradePaymentDTO dto = sandBoxTradePayOrderCmdService.executeSandBoxPay(command);
        return Mono.just(Result.ok(dto.getOrderNo()));
    }

    /**
     * 沙箱 分页查询PaymentLink收款
     */
    @PostMapping("/sandbox/v1/pagePaymentLinkList")
    public Mono<PageResult<SandboxTradePaymentLinkOrderPageDTO>> pageSandboxPaymentLinkList(@RequestBody @Validated TradePaymentLinkPageReq req) {
        log.info("pageSandboxPaymentLinkList req={}", JSONUtil.toJsonStr(req));
        return null;
    }

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
     * 沙箱 API收款-强制成功/失败
     */
    @PostMapping("/sandbox/v1/payForceSuccessOrFailed")
    public Mono<Result<Boolean>> sandboxPayForceSuccessOrFailed(@RequestBody @Validated SandboxTradeForceSuccessReq req) {
        log.info("sandboxPayForceSuccessOrFailed req={}", JSONUtil.toJsonStr(req));
        SandboxTradeForceSuccessCommand command = sandboxTradeConverter.convertSandboxTradeForceSuccessCommand(req);

        boolean successOrFailed = sandBoxTradePayOrderCmdService.sandboxPayForceSuccessOrFailed(command);
        return Mono.just(Result.ok(successOrFailed));
    }

    /**
     * 沙箱 分页查询沙箱收款订单列表
     */
    @PostMapping("/sandbox/v1/pagePaymentOrderList")
    public Mono<PageResult<SandboxTradePaymentOrderPageDTO>> pageSandboxPayOrderList(@RequestBody @Validated SandboxTradePayOrderPageReq req) {
        log.info("pageSandboxPayOrderList req={}", JSONUtil.toJsonStr(req));
        SandboxTradePaymentOrderPageParam param = sandboxTradeConverter.convertSandboxTradePayOrderPageParam(req);

        PageDTO<SandboxTradePaymentOrderPageDTO> pageDTO = sandBoxTradeQueryService.pageSandBoxPayOrderList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    // ============== 收银台相关接口 ==============
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
     * 沙箱 查询收银台支付方式
     */
    @PostMapping("/sandbox/v1/getPaymentMethodList4Cashier")
    public Mono<Result<List<CashierPaymentMethodDTO>>> getSandboxPaymentMethodList4Cashier() {
        log.info("getSandboxPaymentMethodList4Cashier");
        List<CashierPaymentMethodDTO> cashier = sandBoxTradeQueryService.getSandboxPaymentMethodList4Cashier();
        return Mono.just(Result.ok(cashier));
    }

    // ============== 其他接口 ==============
    /**
     * Gateway API历史记录查询状态 根据商户订单号
     */
    @PostMapping("/sandbox/v1/inquiryOrderStatus")
    public Mono<Result<TradeOrderStatusInquiryDTO>> inquirySandboxOrderStatus(@RequestBody @Validated TradeOrderStatusInquiryReq req) {
        log.info("inquiryOrderStatus req={}", JSONUtil.toJsonStr(req));
        TradeOrderStatusInquiryParam param = tradeHistoryConverter.convertTradeOrderStatusInquiryParam(req);

        TradeOrderStatusInquiryDTO inquiryDTO = null;//sandboxTradeHistoryService.inquiryOrderStatus(param);
        return Mono.just(Result.ok(inquiryDTO));
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
}
