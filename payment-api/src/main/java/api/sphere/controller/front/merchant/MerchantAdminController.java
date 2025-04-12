package api.sphere.controller.front.merchant;

import api.sphere.controller.request.*;
import api.sphere.controller.response.*;
import api.sphere.convert.*;
import app.sphere.command.*;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.*;
import app.sphere.query.*;
import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.PaymentMethodRepository;
import infrastructure.sphere.db.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import share.sphere.enums.QuerySourceEnum;
import share.sphere.enums.TradePaymentSourceEnum;
import share.sphere.enums.TradeStatusEnum;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

import java.util.List;

/**
 * 商户管理控制器
 * <p>
 * 提供商户管理、安全认证和支付管理等功能
 * 主要功能包括：
 * 1. 商户管理：基本信息、配置信息、渠道配置等
 * 2. 商户安全：登录认证、密码管理、谷歌验证等
 * 3. 支付管理：支付配置、资金管理、订单管理等
 */
@Slf4j
@RestController
@RequestMapping("/merchant/admin")
public class MerchantAdminController {

    // ===================== 商户管理相关服务 =====================
    @Resource
    MerchantQueryService merchantQueryService;
    @Resource
    MerchantConfigQueryService merchantConfigQueryService;
    @Resource
    MerchantConfigCmdService merchantConfigCmdService;
    @Resource
    MerchantChannelConfigQueryService merchantChannelConfigQueryService;
    @Resource
    MerchantPayoutConfigQueryService merchantPayoutConfigQueryService;
    @Resource
    MerchantWithdrawConfigQueryService merchantWithdrawConfigQueryService;

    // ===================== 商户安全相关服务 =====================
    @Resource
    MerchantLoginCmdService merchantLoginCmdService;

    // ===================== 支付管理相关服务 =====================
    @Resource
    PaymentMethodRepository paymentMethodRepository;
    @Resource
    TradePaymentOrderCmdService tradePaymentOrderCmdService;
    @Resource
    TradePaymentOrderQueryService tradePaymentOrderQueryService;
    @Resource
    TradePayoutOrderQueryService tradePayoutOrderQueryService;
    @Resource
    TradePayoutOrderCmdService tradePayoutOrderCmdService;
    @Resource
    TradeRechargeOrderCmdService tradeRechargeOrderCmdService;
    @Resource
    TradeRechargeOrderQueryService tradeRechargeOrderQueryService;
    @Resource
    TradeTransferOrderCmdService tradeTransferOrderCmdService;
    @Resource
    TradeTransferOrderQueryService tradeTransferOrderQueryService;
    @Resource
    TradeWithdrawOrderCmdService tradeWithdrawOrderCmdService;
    @Resource
    TradeWithdrawOrderQueryService tradeWithdrawOrderQueryService;
    @Resource
    SettleFlowQueryService settleFlowQueryService;
    @Resource
    SettleAccountSnapshotQueryService settleAccountSnapshotQueryService;

    // ===================== 转换器 =====================
    @Resource
    MerchantConverter merchantConverter;
    @Resource
    MerchantConfigConverter merchantConfigConverter;
    @Resource
    MerchantChannelConfigConverter merchantChannelConfigConverter;
    @Resource
    MerchantLoginConverter merchantLoginConverter;
    @Resource
    PaymentMethodConverter paymentMethodConverter;
    @Resource
    TradePayConverter tradePayConverter;
    @Resource
    TradeCashConverter tradePayoutConverter;
    @Resource
    TradeRechargeConverter tradeRechargeConverter;
    @Resource
    TradeTransferConverter tradeTransferConverter;
    @Resource
    TradeWithdrawConverter tradeWithdrawConverter;
    @Resource
    SettleFlowConverter settleFlowConverter;
    @Resource
    SettleAccountSnapshotConverter settleAccountSnapshotConverter;

    // ===================== 商户管理接口 =====================

    /**
     * 获取商户基本信息
     * 
     * @param req 商户ID请求参数
     * @return 商户基本信息视图对象
     */
    @PostMapping("/v1/getMerchant")
    public Mono<Result<MerchantBaseVO>> getBaseMerchant(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户基本信息, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());

        Merchant merchant = merchantQueryService.getMerchant(param);
        MerchantBaseVO vo = merchantConverter.convertMerchantBaseVO(merchant);
        return Mono.just(Result.ok(vo));
    }

    /**
     * 获取商户配置信息
     * 
     * @param req 商户ID请求参数
     * @return 商户配置信息
     */
    @PostMapping("/v1/getMerchantConfig")
    public Mono<Result<MerchantConfigDTO>> getMerchantConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户配置信息, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantIdParam param = merchantConfigConverter.convertMerchantIdParam(req);

        MerchantConfigDTO merchantConfigDTO = merchantConfigQueryService.getMerchantConfig(param);
        return Mono.just(Result.ok(merchantConfigDTO));
    }

    /**
     * 更新商户配置信息
     * 
     * @param req 商户配置更新请求
     * @return 更新结果
     */
    @PostMapping("/v1/updateMerchantConfig")
    public Mono<Result<Boolean>> updateMerchantConfig(@RequestBody @Validated MerchantConfigUpdateReq req) {
        log.info("更新商户配置信息, req={}", JSONUtil.toJsonStr(req));
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantConfigUpdateCmd cmd = merchantConfigConverter.convertMerchantConfigUpdateCmd(req);

        boolean updated = merchantConfigCmdService.updateMerchantConfig(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 获取商户渠道配置列表
     * 
     * @param req 商户渠道配置查询请求
     * @return 商户渠道配置列表
     */
    @PostMapping("/v1/getMerchantChannelConfigList")
    public Mono<Result<MerchantChannelConfigListDTO>> getMerchantChannelConfigList(@RequestBody @Validated MerchantChannelConfigListReq req) {
        log.info("获取商户渠道配置列表, req={}", JSONUtil.toJsonStr(req));
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantChannelConfigListParam param =
                merchantChannelConfigConverter.convertMerchantChannelConfigListParam(req);

        MerchantChannelConfigListDTO configList = merchantChannelConfigQueryService.getMerchantChannelConfigList(param);
        return Mono.just(Result.ok(configList));
    }

    // ===================== 商户安全接口 =====================

    /**
     * 商户登录
     * 
     * @param req 商户登录请求
     * @return 登录结果
     */
    @PostMapping("/v1/merchantLogin")
    public Mono<Result<MerchantLoginDTO>> merchantLogin(@RequestBody @Validated MerchantLoginReq req) {
        log.info("商户登录, username={}", req.getUsername());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantLoginCmd cmd = merchantLoginConverter.convertMerchantLoginCmd(req);

        MerchantLoginDTO dto = merchantLoginCmdService.merchantLogin(cmd);
        return Mono.just(Result.ok(dto));
    }

    /**
     * 验证谷歌验证码
     * 
     * @param req 谷歌验证码验证请求
     * @return 验证结果
     */
    @PostMapping("/v1/verifyGoogleCode")
    public Mono<Result<Boolean>> verifyGoogleCode(@RequestBody @Validated MerchantVerifyGoogleCodeReq req) {
        log.info("验证谷歌验证码, username={}", req.getUsername());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantVerifyGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantVerifyGoogleCodeCommand(req);

        boolean verified = merchantLoginCmdService.verifyGoogleCode(cmd);
        return Mono.just(Result.ok(verified));
    }

    /**
     * 商户忘记密码
     * 
     * @param req 忘记密码请求
     * @return 处理结果
     */
    @PostMapping("/v1/forgetPassword")
    public Mono<Result<Boolean>> forgetPassword(@RequestBody @Validated MerchantPasswordForgetReq req) {
        log.info("商户忘记密码, username={}", req.getUsername());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantPasswordForgetCmd cmd = merchantLoginConverter.convertMerchantPasswordForgetCmd(req);

        boolean forgotten = merchantLoginCmdService.forgetPassword(cmd);
        return Mono.just(Result.ok(forgotten));
    }

    /**
     * 商户修改密码
     * 
     * @param req 修改密码请求
     * @return 修改结果
     */
    @PostMapping("/v1/changePassword")
    public Mono<Result<Boolean>> changePassword(@RequestBody @Validated MerchantPasswordChangeReq req) {
        log.info("商户修改密码, username={}", req.getUsername());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantPasswordChannelCmd cmd = merchantLoginConverter.convertMerchantPasswordChannelCmd(req);

        boolean changed = merchantLoginCmdService.changePassword(cmd);
        return Mono.just(Result.ok(changed));
    }

    /**
     * 展示谷歌验证器二维码
     * 
     * @param req 展示二维码请求
     * @return 二维码内容
     */
    @PostMapping("/v1/showGoogleCode")
    public Mono<Result<String>> showGoogleCode(@RequestBody @Validated MerchantShowGoogleCodeReq req) {
        log.info("展示谷歌验证器二维码, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantShowGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantShowGoogleCodeCmd(req);

        String shown = merchantLoginCmdService.showGoogleAuth(cmd);
        return Mono.just(Result.ok(shown));
    }

    /**
     * 绑定谷歌验证器
     * 
     * @param req 绑定请求
     * @return 绑定结果
     */
    @PostMapping("/v1/setGoogleCode")
    public Mono<Result<Boolean>> setGoogleCode(@RequestBody @Validated MerchantSetGoogleCodeReq req) {
        log.info("绑定谷歌验证器, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantSetGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantSetGoogleCodeCmd(req);

        boolean set = merchantLoginCmdService.setGoogleCode(cmd);
        return Mono.just(Result.ok(set));
    }

    /**
     * 解绑谷歌验证器
     * 
     * @param req 解绑请求
     * @return 解绑结果
     */
    @PostMapping("/v1/unsetGoogleCode")
    public Mono<Result<Boolean>> unsetGoogleCode(@RequestBody @Validated UnsetGoogleCodeReq req) {
        log.info("解绑谷歌验证器, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantUnsetGoogleCodeCmd cmd = merchantLoginConverter.convertMerchantUnsetGoogleCodeCmd(req);

        boolean unset = merchantLoginCmdService.unsetGoogleAuth(cmd);
        return Mono.just(Result.ok(unset));
    }

    // ===================== 支付管理接口 =====================

    // ===================== 支付配置管理 =====================

    /**
     * 获取支付链接配置
     * 
     * @param req 商户ID请求
     * @return 支付链接配置
     */
    @PostMapping("/v1/getPaymentLinkSetting")
    public Mono<Result<MerchantPaymentLinkSettingDTO>> getPaymentLinkSetting(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取支付链接配置, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantIdParam param = merchantConfigConverter.convertMerchantIdParam(req);

        app.sphere.query.dto.MerchantPaymentLinkSettingDTO paymentLinkSetting =
                merchantConfigQueryService.getPaymentLinkSetting(param);
        return Mono.just(Result.ok(paymentLinkSetting));
    }

    /**
     * 更新支付链接配置
     * 
     * @param req 支付链接配置更新请求
     * @return 更新结果
     */
    @PostMapping("/v1/updatePaymentLinkSetting")
    public Mono<Result<Boolean>> updatePaymentLinkSetting(@RequestBody @Validated PaymentLinkSettingReq req) {
        log.info("更新支付链接配置, req={}", JSONUtil.toJsonStr(req));
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        PaymentLinkSettingCmd cmd = merchantConfigConverter.convertPaymentLinkSettingCmd(req);

        boolean updated = merchantConfigCmdService.updatePaymentLinkSetting(cmd);
        return Mono.just(Result.ok(updated));
    }

    /**
     * 获取支付方式下拉列表
     * 
     * @param req 支付方式查询请求
     * @return 支付方式列表
     */
    @PostMapping(value = "/v1/dropPaymentMethodList")
    public Mono<Result<List<PaymentMethodDropVO>>> dropPaymentMethodList(@RequestBody PaymentMethodDropReq req) {
        log.info("获取支付方式下拉列表, req={}", JSONUtil.toJsonStr(req));
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        List<PaymentMethod> methodList = paymentMethodRepository.list();
        List<PaymentMethodDropVO> voList = paymentMethodConverter.convertPaymentMethodDropVOList(methodList);
        return Mono.just(Result.ok(voList));
    }

    /**
     * 获取商户代付配置
     * 
     * @param req 商户ID请求
     * @return 代付配置信息
     */
    @PostMapping("/v1/getMerchantPayoutConfig")
    public Mono<Result<MerchantPayoutConfigDTO>> getMerchantPayoutConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户代付配置, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());

        MerchantPayoutConfigDTO merchantPayoutConfig = merchantPayoutConfigQueryService.getMerchantPayoutConfig(param);
        return Mono.just(Result.ok(merchantPayoutConfig));
    }

    /**
     * 获取商户提现配置
     * 
     * @param req 商户ID请求
     * @return 提现配置信息
     */
    @PostMapping("/v1/getMerchantWithdrawConfig")
    public Mono<Result<MerchantWithdrawConfigDTO>> getMerchantWithdrawConfig(@RequestBody @Validated MerchantIdReq req) {
        log.info("获取商户提现配置, merchantId={}", req.getMerchantId());
        req.setQuerySource(QuerySourceEnum.MERCHANT_ADMIN.getCode());
        MerchantIdParam param = new MerchantIdParam();
        param.setMerchantId(req.getMerchantId());

        MerchantWithdrawConfigDTO withdrawConfig = merchantWithdrawConfigQueryService.getMerchantWithdrawConfig(param);
        return Mono.just(Result.ok(withdrawConfig));
    }

    // ===================== 资金管理 =====================

    /**
     * 分页查询商户资金流水
     * 
     * @param req 资金流水分页查询请求
     * @return 资金流水分页结果
     */
    @PostMapping("/v1/pageAccountFlowList")
    public Mono<PageResult<SettleAccountFlowVO>> pageAccountFlow(@RequestBody @Validated SettleAccountFlowPageReq req) {
        log.info("分页查询商户资金流水, req={}", JSONUtil.toJsonStr(req));
        SettleAccountFlowPageParam param = settleFlowConverter.convertAccountFlowPageParam(req);

        Page<SettleAccountFlow> page = settleFlowQueryService.pageAccountFlowList(param);
        List<SettleAccountFlowVO> voList = settleFlowConverter.convertAccountFlowVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出商户资金流水
     * 
     * @param req 资金流水导出请求
     * @return 导出文件路径
     */
    @PostMapping("/v1/exportAccountFlowList")
    public Mono<Result<String>> exportAccountFlowList(@RequestBody @Validated SettleAccountFlowPageReq req) {
        log.info("导出商户资金流水, req={}", JSONUtil.toJsonStr(req));
        SettleAccountFlowPageParam param = settleFlowConverter.convertAccountFlowPageParam(req);

        String exportAccountFlowList = settleFlowQueryService.exportAccountFlowList(param);
        return Mono.just(Result.ok(exportAccountFlowList));
    }

    /**
     * 查询商户余额及其快照
     * 
     * @param req 账户快照查询请求
     * @return 账户快照信息
     */
    @PostMapping("/v1/getAccountSnapshot")
    public Mono<Result<AccountSnapshotDTO>> getAccountSnapshot(@RequestBody @Validated SettleAccountSnapshotReq req) {
        log.info("查询商户余额及其快照, req={}", JSONUtil.toJsonStr(req));
        SettleAccountSnapshotParam param = settleAccountSnapshotConverter.convertAccountSnapshotParam(req);

        AccountSnapshotDTO accountSnapshot = settleAccountSnapshotQueryService.getAccountSnapshot(param);
        return Mono.just(Result.ok(accountSnapshot));
    }

    // ===================== 订单管理 =====================

    // ===================== 收款订单管理 =====================

    /**
     * 创建支付链接
     * 
     * @param req 支付请求
     * @return 支付链接
     */
    @PostMapping("/v1/createPaymentLink")
    public Mono<Result<String>> createPaymentLink(@RequestBody @Validated TradePaymentReq req) {
        log.info("创建支付链接, req={}", JSONUtil.toJsonStr(req));
        TradePaymentCmd cmd = tradePayConverter.convertTradePayCmd(req);
        cmd.setTradePaySource(TradePaymentSourceEnum.PAY_LINK);

        String paymentLink = tradePaymentOrderCmdService.executePaymentLink(cmd);
        return Mono.just(Result.ok(paymentLink));
    }

    /**
     * 分页查询支付链接
     * 
     * @param req 支付链接分页查询请求
     * @return 支付链接分页结果
     */
    @PostMapping("/v1/pagePaymentLinkList")
    public Mono<PageResult<TradePaymentLinkOrderVO>> pagePaymentLinkList(@RequestBody @Validated TradePaymentLinkPageReq req) {
        log.info("分页查询支付链接, req={}", JSONUtil.toJsonStr(req));
        TradePaymentLinkPageParam param = tradePayConverter.convertTradePaymentLinkPageParam(req);
        Page<TradePaymentLinkOrder> page = null;//tradePayOrderQueryService.pagePaymentLinkList(param);
        List<TradePaymentLinkOrderVO> voList = tradePayConverter.convertTradePaymentLinkOrderVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 分页查询收款订单列表
     * 
     * @param req 收款订单分页查询请求
     * @return 收款订单分页结果
     */
    @PostMapping("/v1/pagePayOrderList")
    public Mono<PageResult<TradePayOrderPageDTO>> pagePayOrderList(@RequestBody @Validated TradePayOrderPageReq req) {
        log.info("分页查询收款订单列表, req={}", JSONUtil.toJsonStr(req));
        TradePayOrderPageParam param = tradePayConverter.convertPageParam(req);

        PageDTO<TradePayOrderPageDTO> pageDTO = null;//tradePayOrderQueryService.pagePayOrderList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 导出收款订单列表
     * 
     * @param req 收款订单导出请求
     * @return 导出文件路径
     */
    @PostMapping("/v1/exportPayOrderList")
    public Mono<Result<String>> exportPayOrderList(@RequestBody @Validated TradePayOrderPageReq req) {
        log.info("导出收款订单列表, req={}", JSONUtil.toJsonStr(req));
        TradePayOrderPageParam param = tradePayConverter.convertPageParam(req);

        String exportPayOrder = null;//tradePayOrderQueryService.exportPayOrderList(param);
        return Mono.just(Result.ok(exportPayOrder));
    }

    /**
     * 查询收款订单详情
     * 
     * @param req 交易单号请求
     * @return 收款订单详情
     */
    @PostMapping("/v1/getPayOrder")
    public Mono<Result<TradePayOrderDTO>> getPayOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("查询收款订单详情, req={}", JSONUtil.toJsonStr(req));
        TradePayOrderDTO dto = null;//tradePayOrderQueryService.getPayOrderByTradeNo(req.getTradeNo());
        return Mono.just(Result.ok(dto));
    }

    // ===================== 代付订单管理 =====================

    /**
     * 分页查询代付订单列表
     * 
     * @param req 代付订单分页查询请求
     * @return 代付订单分页结果
     */
    @PostMapping("/v1/pageCashOrderList")
    public Mono<PageResult<TradeCashOrderPageVO>> pageCashOrderList(@RequestBody @Validated TradeCashOrderPageReq req) {
        log.info("分页查询代付订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeCashOrderPageParam param = tradePayoutConverter.convertPageParam(req);

        PageDTO<TradeCashOrderPageDTO> pageDTO = tradePayoutOrderQueryService.pageCashOrderList(param);
        List<TradeCashOrderPageVO> voList = tradePayoutConverter.convertPageVOList(pageDTO.getData());
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), voList));
    }

    /**
     * 导出代付订单列表
     * 
     * @param req 代付订单导出请求
     * @return 导出文件路径
     */
    @PostMapping("/v1/exportCashOrderList")
    public Mono<Result<String>> exportCashOrderList(@RequestBody @Validated TradeCashOrderPageReq req) {
        log.info("导出代付订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeCashOrderPageParam param = tradePayoutConverter.convertPageParam(req);

        String exportCashOrder = tradePayoutOrderQueryService.exportCashOrderList(param);
        return Mono.just(Result.ok(exportCashOrder));
    }

    /**
     * 查询代付订单详情
     * 
     * @param req 交易单号请求
     * @return 代付订单详情
     */
    @PostMapping("/v1/getCashOrder")
    public Mono<Result<TradeCashOrderDTO>> getCashOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("查询代付订单详情, req={}", JSONUtil.toJsonStr(req));

        TradeCashOrderDTO cashOrderDTO = tradePayoutOrderQueryService.getCashOrderByTradeNo(req.getTradeNo());
        return Mono.just(Result.ok(cashOrderDTO));
    }

    /**
     * 查询代付交易凭证
     * 
     * @param req 交易单号请求
     * @return 代付交易凭证
     */
    @PostMapping("/v1/getCashReceipt")
    public Mono<Result<TradeCashReceiptDTO>> getCashReceipt(@RequestBody @Validated TradeNoReq req) {
        log.info("查询代付交易凭证, req={}", JSONUtil.toJsonStr(req));

        TradeCashReceiptDTO cashReceipt = tradePayoutOrderQueryService.getCashReceipt(req.getTradeNo());
        return Mono.just(Result.ok(cashReceipt));
    }

    // ===================== 充值订单管理 =====================

    /**
     * 充值前置
     * 
     * @param req 充值前置请求
     * @return 充值前置信息
     */
    @PostMapping("/v1/preRecharge")
    public Mono<Result<PreRechargeDTO>> preRecharge(@RequestBody @Validated TradePreRechargeReq req) {
        log.info("充值前置, req={}", JSONUtil.toJsonStr(req));
        TradePreRechargeCommand command = tradeRechargeConverter.convertTradePreRechargeCommand(req);

        PreRechargeDTO preRechargeDTO = tradeRechargeOrderCmdService.executePreRecharge(command);
        return Mono.just(Result.ok(preRechargeDTO));
    }

    /**
     * 充值
     * 
     * @param req 充值请求
     * @return 充值结果
     */
    @PostMapping("/v1/recharge")
    public Mono<Result<Boolean>> recharge(@RequestBody @Validated TradeRechargeReq req) {
        log.info("充值, req={}", JSONUtil.toJsonStr(req));
        TradeRechargeCommand command = tradeRechargeConverter.convertTradeRechargeCommand(req);

        boolean executeRecharge = tradeRechargeOrderCmdService.executeRecharge(command);
        return Mono.just(Result.ok(executeRecharge));
    }

    /**
     * 分页查询充值订单
     * 
     * @param req 充值订单分页查询请求
     * @return 充值订单分页结果
     */
    @PostMapping("/v1/pageRechargeOrderList")
    public Mono<PageResult<TradeRechargeOrderPageVO>> pageRechargeOrderList(@RequestBody @Validated TradeRechargeOrderPageReq req) {
        log.info("分页查询充值订单, req={}", JSONUtil.toJsonStr(req));
        TradeRechargeOrderPageParam param = tradeRechargeConverter.convertTradeRechargeOrderPageParam(req);

        Page<TradeRechargeOrder> page = null;//tradeRechargeOrderQueryService.pageRechargeOrderList(param);
        List<TradeRechargeOrderPageVO> voList =
                tradeRechargeConverter.convertTradeRechargeOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出充值订单
     * 
     * @param req 充值订单导出请求
     * @return 导出文件路径
     */
    @PostMapping("/v1/exportRechargeOrderList")
    public Mono<Result<String>> exportRechargeOrderList(@RequestBody @Validated TradeRechargeOrderPageReq req) {
        log.info("导出充值订单, req={}", JSONUtil.toJsonStr(req));
        TradeRechargeOrderPageParam param = tradeRechargeConverter.convertTradeRechargeOrderPageParam(req);

        String exportRechargeOrder = null;//tradeRechargeOrderQueryService.exportRechargeOrderList(param);
        return Mono.just(Result.ok(exportRechargeOrder));
    }

    /**
     * 再次审核充值
     * 
     * @param req 充值请求
     * @return 审核结果
     */
    @PostMapping("/v1/reviewRecharge")
    public Mono<Result<Boolean>> reviewRecharge(@RequestBody @Validated TradeRechargeReq req) {
        log.info("再次审核充值, req={}", JSONUtil.toJsonStr(req));
        String tradeNo = req.getTradeNo();

        boolean reviewRecharge = tradeRechargeOrderCmdService.reviewRecharge(tradeNo);
        return Mono.just(Result.ok(reviewRecharge));
    }

    // ===================== 转账订单管理 =====================

    /**
     * API转账
     * 
     * @param req 转账请求
     * @return 转账结果
     */
    @PostMapping("/v1/transfer")
    public Mono<Result<Boolean>> transfer(@RequestBody @Validated TradeTransferReq req) {
        log.info("API转账, req={}", JSONUtil.toJsonStr(req));
        TradeTransferCommand command = tradeTransferConverter.convertTradeTransferCommand(req);

        boolean executeTransfer = tradeTransferOrderCmdService.executeTransfer(command);
        return Mono.just(Result.ok(executeTransfer));
    }

    /**
     * 分页查询转账订单列表
     * 
     * @param req 转账订单分页查询请求
     * @return 转账订单分页结果
     */
    @PostMapping("/v1/pageTransferOrderList")
    public Mono<PageResult<TradeTransferOrderPageVO>> pageTransferOrderList(@RequestBody @Validated TradeTransferOrderPageReq req) {
        log.info("分页查询转账订单列表, req={}", JSONUtil.toJsonStr(req));
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
     * 
     * @param req 转账订单导出请求
     * @return 导出文件路径
     */
    @PostMapping("/v1/exportTransferOrderList")
    public Mono<Result<String>> exportTransferOrderList(@RequestBody @Validated TradeTransferOrderPageReq req) {
        log.info("导出转账订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeTransferOrderPageParam param = tradeTransferConverter.convertTradeTransferOrderPageParam(req);

        String exportTransferOrder = tradeTransferOrderQueryService.exportTransferOrderList(param);
        return Mono.just(Result.ok(exportTransferOrder));
    }

    /**
     * 查询转账订单详情
     * 
     * @param req 交易单号请求
     * @return 转账订单详情
     */
    @PostMapping("/v1/getTransferOrder")
    public Mono<Result<TradeTransferOrderDTO>> getTransferOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("查询转账订单详情, req={}", JSONUtil.toJsonStr(req));

        TradeTransferOrderDTO dto = tradeTransferOrderQueryService.getTransferOrderByTradeNo(req.getTradeNo());
        return Mono.just(Result.ok(dto));
    }

    // ===================== 提现订单管理 =====================

    /**
     * 提现打款
     * 
     * @param req 提现请求
     * @return 提现结果
     */
    @PostMapping("/v1/withdraw")
    public Mono<Result<Boolean>> withdraw(@RequestBody @Validated TradeWithdrawReq req) {
        log.info("提现打款, req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawCommand command = tradeWithdrawConverter.convertTradeWithdrawCommand(req);

        boolean executeWithdraw = tradeWithdrawOrderCmdService.executeWithdraw(command);
        return Mono.just(Result.ok(executeWithdraw));
    }

    /**
     * 分页查询提现订单
     * 
     * @param req 提现订单分页查询请求
     * @return 提现订单分页结果
     */
    @PostMapping("/v1/pageWithdrawOrderList")
    public Mono<PageResult<TradeWithdrawOrderPageVO>> pageWithdrawOrderList(@RequestBody @Validated TradeWithdrawOrderPageReq req) {
        log.info("分页查询提现订单, req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawOrderPageParam param = tradeWithdrawConverter.convertTradeWithdrawOrderPageParam(req);

        Page<TradeWithdrawOrder> page = tradeWithdrawOrderQueryService.pageWithdrawOrderList(param);
        List<TradeWithdrawOrderPageVO> voList =
                tradeWithdrawConverter.convertTradeWithdrawOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出提现订单
     * 
     * @param req 提现订单导出请求
     * @return 导出文件路径
     */
    @PostMapping("/v1/exportWithdrawOrderList")
    public Mono<Result<String>> exportWithdrawOrderList(@RequestBody @Validated TradeWithdrawOrderPageReq req) {
        log.info("导出提现订单, req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawOrderPageParam param = tradeWithdrawConverter.convertTradeWithdrawOrderPageParam(req);

        String exportWithdrawOrder = tradeWithdrawOrderQueryService.exportWithdrawOrderList(param);
        return Mono.just(Result.ok(exportWithdrawOrder));
    }

    /**
     * 查询提现订单
     * 
     * @param req 交易单号请求
     * @return 提现订单详情
     */
    @PostMapping("/v1/getWithdrawOrder")
    public Mono<Result<TradeWithdrawOrderDTO>> getWithdrawOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("查询提现订单, req={}", JSONUtil.toJsonStr(req));

        TradeWithdrawOrderDTO withdrawOrderDTO = tradeWithdrawOrderQueryService.getWithdrawOrder(req.getTradeNo());
        return Mono.just(Result.ok(withdrawOrderDTO));
    }

    /**
     * 查询自动提现/转账标签
     * 
     * @param req 标签查询请求
     * @return 标签状态
     */
    @PostMapping("/v1/getWithdrawFlag")
    public Mono<Result<Boolean>> getWithdrawFlag(@RequestBody @Validated WithdrawFlagReq req) {
        log.info("查询自动提现/转账标签, req={}", JSONUtil.toJsonStr(req));
        WithdrawFlagParam param = tradeWithdrawConverter.convertWithdrawFlagParam(req);

        boolean withdrawFlag = tradeWithdrawOrderQueryService.getWithdrawFlag(param);
        return Mono.just(Result.ok(withdrawFlag));
    }
}
