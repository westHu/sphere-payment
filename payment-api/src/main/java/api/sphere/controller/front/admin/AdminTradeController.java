package api.sphere.controller.front.admin;

import api.sphere.controller.request.*;
import api.sphere.controller.response.*;
import api.sphere.convert.*;
import app.sphere.command.*;
import app.sphere.command.cmd.*;
import app.sphere.query.*;
import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import infrastructure.sphere.db.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import share.sphere.enums.TradePaymentSourceEnum;
import share.sphere.result.PageResult;
import share.sphere.result.Result;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * 管理后台交易相关接口
 * 
 * 本控制器提供交易系统管理所需的所有接口，主要包括以下功能模块：
 * 1. 交易回调管理
 *    - 订单补发回调
 * 2. 收款管理
 *    - PaymentLink收款
 *    - 收款补单
 *    - 收款退单
 *    - 收款订单查询
 * 3. 代付管理
 *    - 代付补单
 *    - 代付退单
 *    - 代付订单查询
 * 4. 充值管理
 *    - 充值订单查询
 *    - 充值审核
 * 5. 交易审核管理
 *    - 交易审核
 * 6. 交易统计管理
 *    - 平台交易统计
 *    - 渠道交易统计
 *    - 商户交易统计
 * 7. 转账管理
 *    - 转账操作
 *    - 转账订单查询
 * 8. 提现管理
 *    - 提现订单查询
 *    - 提现标记
 * 
 * 所有接口均采用POST方式，返回Mono<Result<T>>或Mono<PageResult<T>>格式
 */
@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminTradeController {

    // ===================== 依赖注入 =====================
    
    // 交易回调相关服务
    @Resource
    TradeCallBackCmdService tradeCallBackCmdService;
    @Resource
    TradeCallbackConverter tradeCallbackConverter;

    // 收款相关服务
    @Resource
    TradePaymentOrderCmdService tradePaymentOrderCmdService;
    @Resource
    TradePaymentOrderQueryService tradePaymentOrderQueryService;
    @Resource
    TradePaymentConverter tradePaymentConverter;

    // 代付相关服务
    @Resource
    TradePayoutOrderQueryService tradePayoutOrderQueryService;
    @Resource
    TradePayoutOrderCmdService tradePayoutOrderCmdService;
    @Resource
    TradePayoutConverter tradePayoutConverter;

    // 充值相关服务
    @Resource
    TradeRechargeOrderCmdService tradeRechargeOrderCmdService;
    @Resource
    TradeRechargeOrderQueryService tradeRechargeOrderQueryService;
    @Resource
    TradeRechargeConverter tradeRechargeConverter;

    // 交易审核相关服务
    @Resource
    TradeReviewCmdService tradeReviewCmdService;
    @Resource
    TradeReviewConverter tradeReviewConverter;

    // 交易统计相关服务
    @Resource
    SnapshotTradeStatisticsConverter snapshotTradeStatisticsConverter;
    @Resource
    SnapshotTradeStatisticsQueryService snapshotTradeStatisticsQueryService;

    // 转账相关服务
    @Resource
    TradeTransferConverter tradeTransferConverter;
    @Resource
    TradeTransferOrderCmdService tradeTransferOrderCmdService;
    @Resource
    TradeTransferOrderQueryService tradeTransferOrderQueryService;

    // 提现相关服务
    @Resource
    TradeWithdrawOrderCmdService tradeWithdrawOrderCmdService;
    @Resource
    TradeWithdrawOrderQueryService tradeWithdrawOrderQueryService;
    @Resource
    TradeWithdrawConverter tradeWithdrawConverter;

    // ===================== 交易回调管理接口 =====================

    /**
     * 订单补发回调通知
     * 
     * @param req 回调请求
     * @return 回调结果
     */
    @PostMapping("/v1/callback")
    public Callable<Result<Boolean>> callback(@RequestBody @Validated TradeCallbackReq req) {
        log.info("订单补发回调通知, req={}", JSONUtil.toJsonStr(req));
        TradeCallbackCommand cmd = tradeCallbackConverter.convertTradeCallbackCommand(req);
        return () -> Result.ok(tradeCallBackCmdService.handlerTradeCallback(cmd));
    }

    // ===================== 收款管理接口 =====================

    /**
     * 创建PaymentLink收款
     * 
     * @param req 收款请求
     * @return 收款链接
     */
    @PostMapping("/v1/createPaymentLink")
    public Mono<Result<String>> createPaymentLink(@RequestBody @Validated TradePaymentReq req) {
        log.info("创建PaymentLink收款, req={}", JSONUtil.toJsonStr(req));
        TradePaymentCmd cmd = tradePaymentConverter.convertTradePaymentCmd(req);
        cmd.setTradePaySource(TradePaymentSourceEnum.PAY_LINK);
        return Mono.just(Result.ok(tradePaymentOrderCmdService.executePaymentLink(cmd)));
    }

    /**
     * 收款补单
     * 
     * @param req 补单请求
     * @return 补单结果
     */
    @PostMapping("/v1/paymentSupplement")
    public Mono<Result<Boolean>> paymentSupplement(@RequestBody @Validated TradePaymentSupplementReq req) {
        log.info("收款补单, req={}", JSONUtil.toJsonStr(req));
        TradePaymentSupplementCmd cmd = tradePaymentConverter.convertTradePaymentSupplementCmd(req);
        return Mono.just(Result.ok(tradePaymentOrderCmdService.executePaymentSupplement(cmd)));
    }

    /**
     * 收款退单
     * 
     * @param req 退单请求
     * @return 退单结果
     */
    @PostMapping("/v1/paymentRefund")
    public Mono<Result<Boolean>> paymentRefund(@RequestBody @Validated TradePaymentRefundReq req) {
        log.info("收款退单, req={}", JSONUtil.toJsonStr(req));
        TradePaymentRefundCmd cmd = tradePaymentConverter.convertTradePaymentRefundCmd(req);
        return Mono.just(Result.ok(tradePaymentOrderCmdService.executePaymentRefund(cmd)));
    }

    /**
     * 分页查询PaymentLink收款
     * 
     * @param req 分页查询请求
     * @return 收款分页列表
     */
    @PostMapping("/v1/pagePaymentLinkList")
    public Mono<PageResult<TradePaymentLinkOrderVO>> pagePaymentLinkList(@RequestBody @Validated TradePaymentLinkPageReq req) {
        log.info("分页查询PaymentLink收款, req={}", JSONUtil.toJsonStr(req));
        return null;
    }

    /**
     * 分页查询收款订单列表
     * 
     * @param req 分页查询请求
     * @return 订单分页列表
     */
    @PostMapping("/v1/pagePaymentOrderList")
    public Mono<PageResult<TradePaymentOrderPageDTO>> pagePayOrderList(@RequestBody @Validated TradePayOrderPageReq req) {
        log.info("分页查询收款订单列表, req={}", JSONUtil.toJsonStr(req));
        TradePaymentOrderPageParam param = tradePaymentConverter.convertTradePaymentOrderPageParam(req);
        PageDTO<TradePaymentOrderPageDTO> pageDTO = null;//tradePayOrderQueryService.pagePaymentOrderList(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 导出收款订单列表
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportPaymentOrderList")
    public Mono<Result<String>> exportPayOrderList(@RequestBody @Validated TradePayOrderPageReq req) {
        log.info("导出收款订单列表, req={}", JSONUtil.toJsonStr(req));
        TradePaymentOrderPageParam param = tradePaymentConverter.convertTradePaymentOrderPageParam(req);
        String exportPayOrder = null;//tradePayOrderQueryService.exportPaymentOrderList(param);
        return Mono.just(Result.ok(exportPayOrder));
    }

    /**
     * 获取收款订单详情
     * 
     * @param req 查询请求
     * @return 订单详情
     */
    @PostMapping("/v1/getPayOrder")
    public Mono<Result<TradePaymentOrderDTO>> getPayOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("获取收款订单详情, req={}", JSONUtil.toJsonStr(req));
        TradePaymentOrderDTO dto = null;//tradePayOrderQueryService.getPaymentOrderByTradeNo(req.getTradeNo());
        return Mono.just(Result.ok(dto));
    }

    // ===================== 代付管理接口 =====================

    /**
     * 代付补单
     * 
     * @param req 补单请求
     * @return 补单结果
     */
    @PostMapping("/v1/cashSupplement")
    public Mono<Result<Boolean>> cashSupplement(@RequestBody @Validated TradeCashSupplementReq req) {
        log.info("代付补单, req={}", JSONUtil.toJsonStr(req));
        TradeCashSupplementCommand command = tradePayoutConverter.convertTradeCashSupplementCommand(req);
        return Mono.just(Result.ok(tradePayoutOrderCmdService.executeCashSupplement(command)));
    }

    /**
     * 代付退单
     * 
     * @param req 退单请求
     * @return 退单结果
     */
    @PostMapping("/v1/cashRefund")
    public Mono<Result<Boolean>> cashRefund(@RequestBody @Validated TradeCashRefundReq req) {
        log.info("代付退单, req={}", JSONUtil.toJsonStr(req));
        TradeCashRefundCommand command = tradePayoutConverter.convertTradeCashRefundCommand(req);
        return Mono.just(Result.ok(tradePayoutOrderCmdService.executeCashRefund(command)));
    }

    /**
     * 分页查询代付订单列表
     * 
     * @param req 分页查询请求
     * @return 订单分页列表
     */
    @PostMapping("/v1/pagePayoutOrderList")
    public Mono<PageResult<TradeCashOrderPageVO>> pageCashOrderList(@RequestBody @Validated TradePayoutOrderPageReq req) {
        log.info("分页查询代付订单列表, req={}", JSONUtil.toJsonStr(req));
        TradePayoutOrderPageParam param = tradePayoutConverter.convertTradeCashOrderPageParam(req);
        PageDTO<TradePayoutOrderPageDTO> pageDTO = tradePayoutOrderQueryService.pagePayoutOrderList(param);
        List<TradeCashOrderPageVO> voList = tradePayoutConverter.convertTradeCashOrderPageVOList(pageDTO.getData());
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), voList));
    }

    /**
     * 导出代付订单列表
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportPayoutOrderList")
    public Mono<Result<String>> exportCashOrderList(@RequestBody @Validated TradePayoutOrderPageReq req) {
        log.info("导出代付订单列表, req={}", JSONUtil.toJsonStr(req));
        TradePayoutOrderPageParam param = tradePayoutConverter.convertTradeCashOrderPageParam(req);
        return Mono.just(Result.ok(tradePayoutOrderQueryService.exportPayoutOrderList(param)));
    }

    /**
     * 获取代付订单详情
     * 
     * @param req 查询请求
     * @return 订单详情
     */
    @PostMapping("/v1/getCashOrder")
    public Mono<Result<TradePayoutOrderDTO>> getCashOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("获取代付订单详情, req={}", JSONUtil.toJsonStr(req));
        return Mono.just(Result.ok(tradePayoutOrderQueryService.getPayoutOrderByTradeNo(req.getTradeNo())));
    }

    /**
     * 获取代付回单
     * 
     * @param req 查询请求
     * @return 回单信息
     */
    @PostMapping("/v1/getPayoutReceipt")
    public Mono<Result<TradePayoutReceiptDTO>> getCashReceipt(@RequestBody @Validated TradeNoReq req) {
        log.info("获取代付回单, req={}", JSONUtil.toJsonStr(req));
        return Mono.just(Result.ok(tradePayoutOrderQueryService.getPayoutReceipt(req.getTradeNo())));
    }

    // ===================== 充值管理接口 =====================

    /**
     * 分页查询充值订单列表
     * 
     * @param req 分页查询请求
     * @return 订单分页列表
     */
    @PostMapping("/v1/pageRechargeOrderList")
    public Mono<PageResult<TradeRechargeOrderPageVO>> pageRechargeOrderList(@RequestBody @Validated TradeRechargeOrderPageReq req) {
        log.info("分页查询充值订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeRechargeOrderPageParam param = tradeRechargeConverter.convertTradeRechargeOrderPageParam(req);
        Page<TradeRechargeOrder> page = tradeRechargeOrderQueryService.pageRechargeOrderList(param);
        List<TradeRechargeOrderPageVO> voList = tradeRechargeConverter.convertTradeRechargeOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出充值订单列表
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportRechargeOrderList")
    public Mono<Result<String>> exportRechargeOrderList(@RequestBody @Validated TradeRechargeOrderPageReq req) {
        log.info("导出充值订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeRechargeOrderPageParam param = tradeRechargeConverter.convertTradeRechargeOrderPageParam(req);
        return Mono.just(Result.ok(tradeRechargeOrderQueryService.exportRechargeOrderList(param)));
    }

    /**
     * 审核充值订单
     * 
     * @param req 审核请求
     * @return 审核结果
     */
    @PostMapping("/v1/reviewRecharge")
    public Mono<Result<Boolean>> reviewRecharge(@RequestBody @Validated TradeRechargeReq req) {
        log.info("审核充值订单, req={}", JSONUtil.toJsonStr(req));
        return Mono.just(Result.ok(tradeRechargeOrderCmdService.reviewRecharge(req.getTradeNo())));
    }

    // ===================== 交易审核管理接口 =====================

    /**
     * 交易审核
     * 
     * @param req 审核请求
     * @return 审核结果
     */
    @PostMapping("/v1/tradeReview")
    public Mono<Result<Boolean>> tradeReview(@RequestBody @Validated TradeReviewReq req) {
        log.info("交易审核, req={}", JSONUtil.toJsonStr(req));
        TradeReviewCommand command = tradeReviewConverter.convertTradeReviewCommand(req);
        return Mono.just(Result.ok(tradeReviewCmdService.executeTradeReview(command)));
    }

    // ===================== 交易统计管理接口 =====================

    /**
     * 获取平台交易统计
     * 
     * @param req 统计查询请求
     * @return 统计分页列表
     */
    @PostMapping("/v1/getPlatformTradeStatistics")
    public Mono<PageResult<TradePlatformDailySnapchatDTO>> getPlatformTradeStatistics(@RequestBody @Validated TradeStatisticsPlatformReq req) {
        log.info("获取平台交易统计, req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsPlatformParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsPlatformParam(req);
        PageDTO<TradePlatformDailySnapchatDTO> pageDTO = snapshotTradeStatisticsQueryService.getPlatformTradeStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 导出平台交易统计
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportPlatformTradeStatistics")
    public Mono<Result<String>> exportPlatformTradeStatistics(@RequestBody @Validated TradeStatisticsPlatformReq req) {
        log.info("导出平台交易统计, req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsPlatformParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsPlatformParam(req);
        return Mono.just(Result.ok(snapshotTradeStatisticsQueryService.exportPlatformTradeStatistics(param)));
    }

    /**
     * 获取渠道交易统计
     * 
     * @param req 统计查询请求
     * @return 统计分页列表
     */
    @PostMapping("/v1/getChannelTradeStatistics")
    public Mono<PageResult<TradeChannelDailySnapchatDTO>> getChannelTradeStatistics(@RequestBody @Validated TradeStatisticsChannelReq req) {
        log.info("获取渠道交易统计, req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsChannelParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsChannelParam(req);
        PageDTO<TradeChannelDailySnapchatDTO> pageDTO = snapshotTradeStatisticsQueryService.getChannelTradeStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 导出渠道交易统计
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportChannelTradeStatistics")
    public Mono<Result<String>> exportChannelTradeStatistics(@RequestBody @Validated TradeStatisticsChannelReq req) {
        log.info("导出渠道交易统计, req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsChannelParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsChannelParam(req);
        return Mono.just(Result.ok(snapshotTradeStatisticsQueryService.exportChannelTradeStatistics(param)));
    }

    /**
     * 获取商户交易统计
     * 
     * @param req 统计查询请求
     * @return 统计分页列表
     */
    @PostMapping("/v1/getMerchantTradeStatistics")
    public Mono<PageResult<TradeMerchantDailySnapchatDTO>> getMerchantTradeStatistics(@RequestBody @Validated TradeStatisticsMerchantReq req) {
        log.info("获取商户交易统计, req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsMerchantParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsMerchantParam(req);
        PageDTO<TradeMerchantDailySnapchatDTO> pageDTO = snapshotTradeStatisticsQueryService.getMerchantTradeStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 导出商户交易统计
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportMerchantTradeStatistics")
    public Mono<Result<String>> exportMerchantTradeStatistics(@RequestBody @Validated TradeStatisticsMerchantReq req) {
        log.info("导出商户交易统计, req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsMerchantParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsMerchantParam(req);
        return Mono.just(Result.ok(snapshotTradeStatisticsQueryService.exportMerchantTradeStatistics(param)));
    }

    /**
     * 获取商户交易统计指标
     * 
     * @param req 统计查询请求
     * @return 统计指标
     */
    @PostMapping("/v1/getMerchantTradeStatistics4Index")
    public Mono<Result<TradeMerchantStatisticsDTO>> getMerchantTradeStatistics4Index(@RequestBody @Validated TradeMerchantStatisticsSnapshotReq req) {
        log.info("获取商户交易统计指标, req={}", JSONUtil.toJsonStr(req));
        TradeMerchantStatisticsSnapshotParam param = snapshotTradeStatisticsConverter.convertTradeMerchantStatisticsSnapshotParam(req);
        return Mono.just(Result.ok(snapshotTradeStatisticsQueryService.getMerchantTradeStatistics4Index(param)));
    }

    /**
     * 获取交易实时统计指标
     * 
     * @param req 统计查询请求
     * @return 统计指标
     */
    @PostMapping("/v1/getTradeTimelyStatistics4Index")
    public Mono<Result<TradeTimelyStatisticsIndexDTO>> getTradeTimelyStatistics4Index(@RequestBody @Validated TradeTimelyStatisticsIndexReq req) {
        log.info("获取交易实时统计指标, req={}", JSONUtil.toJsonStr(req));
        TradeTimelyStatisticsIndexParam param = snapshotTradeStatisticsConverter.convertTradeTimelyStatisticsIndexParam(req);
        return Mono.just(Result.ok(snapshotTradeStatisticsQueryService.getTradeTimelyStatistics4Index(param)));
    }

    // ===================== 转账管理接口 =====================

    /**
     * 执行转账
     * 
     * @param req 转账请求
     * @return 转账结果
     */
    @PostMapping("/v1/transfer")
    public Mono<Result<Boolean>> transfer(@RequestBody @Validated TradeTransferReq req) {
        log.info("执行转账, req={}", JSONUtil.toJsonStr(req));
        TradeTransferCommand command = tradeTransferConverter.convertTradeTransferCommand(req);
        return Mono.just(Result.ok(tradeTransferOrderCmdService.executeTransfer(command)));
    }

    /**
     * 分页查询转账订单列表
     * 
     * @param req 分页查询请求
     * @return 订单分页列表
     */
    @PostMapping("/v1/pageTransferOrderList")
    public Mono<PageResult<TradeTransferOrderPageVO>> pageTransferOrderList(@RequestBody @Validated TradeTransferOrderPageReq req) {
        log.info("分页查询转账订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeTransferOrderPageParam param = tradeTransferConverter.convertTradeTransferOrderPageParam(req);
        Page<TradeTransferOrder> page = tradeTransferOrderQueryService.pageTransferOrderList(param);
        List<TradeTransferOrderPageVO> voList = tradeTransferConverter.convertTradeTransferOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出转账订单列表
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportTransferOrderList")
    public Mono<Result<String>> exportTransferOrderList(@RequestBody @Validated TradeTransferOrderPageReq req) {
        log.info("导出转账订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeTransferOrderPageParam param = tradeTransferConverter.convertTradeTransferOrderPageParam(req);
        return Mono.just(Result.ok(tradeTransferOrderQueryService.exportTransferOrderList(param)));
    }

    /**
     * 获取转账订单详情
     * 
     * @param req 查询请求
     * @return 订单详情
     */
    @PostMapping("/v1/getTransferOrder")
    public Mono<Result<TradeTransferOrderDTO>> getTransferOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("获取转账订单详情, req={}", JSONUtil.toJsonStr(req));
        return Mono.just(Result.ok(tradeTransferOrderQueryService.getTransferOrderByTradeNo(req.getTradeNo())));
    }

    // ===================== 提现管理接口 =====================

    /**
     * 分页查询提现订单列表
     * 
     * @param req 分页查询请求
     * @return 订单分页列表
     */
    @PostMapping("/v1/pageWithdrawOrderList")
    public Mono<PageResult<TradeWithdrawOrderPageVO>> pageWithdrawOrderList(@RequestBody @Validated TradeWithdrawOrderPageReq req) {
        log.info("分页查询提现订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawOrderPageParam param = tradeWithdrawConverter.convertTradeWithdrawOrderPageParam(req);
        Page<TradeWithdrawOrder> page = tradeWithdrawOrderQueryService.pageWithdrawOrderList(param);
        List<TradeWithdrawOrderPageVO> voList = tradeWithdrawConverter.convertTradeWithdrawOrderPageVOList(page.getRecords());
        return Mono.just(PageResult.ok(page.getTotal(), page.getCurrent(), voList));
    }

    /**
     * 导出提现订单列表
     * 
     * @param req 导出请求
     * @return 导出结果
     */
    @PostMapping("/v1/exportWithdrawOrderList")
    public Mono<Result<String>> exportWithdrawOrderList(@RequestBody @Validated TradeWithdrawOrderPageReq req) {
        log.info("导出提现订单列表, req={}", JSONUtil.toJsonStr(req));
        TradeWithdrawOrderPageParam param = tradeWithdrawConverter.convertTradeWithdrawOrderPageParam(req);
        return Mono.just(Result.ok(tradeWithdrawOrderQueryService.exportWithdrawOrderList(param)));
    }

    /**
     * 获取提现订单详情
     * 
     * @param req 查询请求
     * @return 订单详情
     */
    @PostMapping("/v1/getWithdrawOrder")
    public Mono<Result<TradeWithdrawOrderDTO>> getWithdrawOrder(@RequestBody @Validated TradeNoReq req) {
        log.info("获取提现订单详情, req={}", JSONUtil.toJsonStr(req));
        return Mono.just(Result.ok(tradeWithdrawOrderQueryService.getWithdrawOrder(req.getTradeNo())));
    }

    /**
     * 获取提现标记
     * 
     * @param req 查询请求
     * @return 提现标记
     */
    @PostMapping("/v1/getWithdrawFlag")
    public Mono<Result<Boolean>> getWithdrawFlag(@RequestBody @Validated TradeWithdrawFlagReq req) {
        log.info("获取提现标记, req={}", JSONUtil.toJsonStr(req));
        WithdrawFlagParam param = tradeWithdrawConverter.convertWithdrawFlagParam(req);
        return Mono.just(Result.ok(tradeWithdrawOrderQueryService.getWithdrawFlag(param)));
    }
}
