package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.controller.request.TradeMerchantStatisticsSnapshotReq;
import com.paysphere.controller.request.TradeStatisticsAgentPageReq;
import com.paysphere.controller.request.TradeStatisticsByAgentPageReq;
import com.paysphere.controller.request.TradeStatisticsChannelReq;
import com.paysphere.controller.request.TradeStatisticsMerchantReq;
import com.paysphere.controller.request.TradeStatisticsPlatformReq;
import com.paysphere.controller.request.TradeStatisticsTransferReq;
import com.paysphere.controller.request.TradeTimelyStatisticsIndexReq;
import com.paysphere.convert.SnapshotTradeStatisticsConverter;
import com.paysphere.query.SnapshotTradeStatisticsQueryService;
import com.paysphere.query.dto.PageDTO;
import com.paysphere.query.dto.TradeChannelDailySnapchatDTO;
import com.paysphere.query.dto.TradeMerchantDailySnapchatDTO;
import com.paysphere.query.dto.TradeMerchantStatisticsByAgentDTO;
import com.paysphere.query.dto.TradeMerchantStatisticsDTO;
import com.paysphere.query.dto.TradePlatformDailySnapchatDTO;
import com.paysphere.query.dto.TradeStatisticsAgentDTO;
import com.paysphere.query.dto.TradeTimelyStatisticsIndexDTO;
import com.paysphere.query.dto.TransferDailySnapchatDTO;
import com.paysphere.query.param.TradeMerchantStatisticsSnapshotParam;
import com.paysphere.query.param.TradeStatisticsAgentPageParam;
import com.paysphere.query.param.TradeStatisticsByAgentPageParam;
import com.paysphere.query.param.TradeStatisticsChannelParam;
import com.paysphere.query.param.TradeStatisticsMerchantParam;
import com.paysphere.query.param.TradeStatisticsPlatformParam;
import com.paysphere.query.param.TradeStatisticsTransferParam;
import com.paysphere.query.param.TradeTimelyStatisticsIndexParam;
import com.paysphere.result.PageResult;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * 交易分析API
 */

@Slf4j
@RestController
public class TradeStatisticsController {

    @Resource
    SnapshotTradeStatisticsConverter snapshotTradeStatisticsConverter;
    @Resource
    SnapshotTradeStatisticsQueryService snapshotTradeStatisticsQueryService;

    /**
     * 平台交易分析数据（收款、代付）
     * 平台每日交易数据
     * 日期、成功数量、成功金额、成功率等等等
     */
    @PostMapping("/v1/getPlatformTradeStatistics")
    public Mono<PageResult<TradePlatformDailySnapchatDTO>> getPlatformTradeStatistics(@RequestBody @Validated
                                                                                      TradeStatisticsPlatformReq req) {
        log.info("getPlatformTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsPlatformParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsPlatformParam(req);

        PageDTO<TradePlatformDailySnapchatDTO> pageDTO = snapshotTradeStatisticsQueryService.getPlatformTradeStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }


    /**
     * 导出  平台交易分析数据（收款、代付）
     * 平台每日交易数据
     * 日期、成功数量、成功金额、成功率等等等
     */
    @PostMapping("/v1/exportPlatformTradeStatistics")
    public Mono<Result<String>> exportPlatformTradeStatistics(@RequestBody @Validated TradeStatisticsPlatformReq req) {
        log.info("exportPlatformTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsPlatformParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsPlatformParam(req);

        String exportPlatformTradeStatistics = snapshotTradeStatisticsQueryService.exportPlatformTradeStatistics(param);
        return Mono.just(Result.ok(exportPlatformTradeStatistics));
    }


    /**
     * 通道交易分析数据 （收款、代付）
     */
    @PostMapping("/v1/getChannelTradeStatistics")
    public Mono<PageResult<TradeChannelDailySnapchatDTO>> getChannelTradeStatistics(
            @RequestBody @Validated TradeStatisticsChannelReq req) {
        log.info("getChannelTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsChannelParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsChannelParam(req);

        PageDTO<TradeChannelDailySnapchatDTO> pageDTO = snapshotTradeStatisticsQueryService.getChannelTradeStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }


    /**
     * 导出  通道交易分析数据 （收款、代付）
     */
    @PostMapping("/v1/exportChannelTradeStatistics")
    public Mono<Result<String>> exportChannelTradeStatistics(@RequestBody @Validated
                                                                 TradeStatisticsChannelReq req) {
        log.info("exportChannelTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsChannelParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsChannelParam(req);

        String exportChannelTradeStatistics = snapshotTradeStatisticsQueryService.exportChannelTradeStatistics(param);
        return Mono.just(Result.ok(exportChannelTradeStatistics));
    }


    /**
     * 商户交易分析数据 （收款、代付）
     */
    @PostMapping("/v1/getMerchantTradeStatistics")
    public Mono<PageResult<TradeMerchantDailySnapchatDTO>> getMerchantTradeStatistics(
            @RequestBody @Validated TradeStatisticsMerchantReq req) {
        log.info("getMerchantTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsMerchantParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsMerchantParam(req);

        PageDTO<TradeMerchantDailySnapchatDTO> pageDTO = snapshotTradeStatisticsQueryService.getMerchantTradeStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }


    /**
     * 导出  商户交易分析数据 （收款、代付）
     */
    @PostMapping("/v1/exportMerchantTradeStatistics")
    public Mono<Result<String>> exportMerchantTradeStatistics(@RequestBody @Validated
                                                                  TradeStatisticsMerchantReq req) {
        log.info("exportMerchantTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsMerchantParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsMerchantParam(req);

        String exportMerchantTradeStatistics = snapshotTradeStatisticsQueryService.exportMerchantTradeStatistics(param);
        return Mono.just(Result.ok(exportMerchantTradeStatistics));
    }


    /**
     * 平台/商户交易分析数据 （转账）
     */
    @PostMapping("/v1/getTransferStatistics")
    public Mono<PageResult<TransferDailySnapchatDTO>> getTransferStatistics(@RequestBody @Validated
                                                                            TradeStatisticsTransferReq req) {
        log.info("getTransferStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsTransferParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsTransferParam(req);

        PageDTO<TransferDailySnapchatDTO> pageDTO = snapshotTradeStatisticsQueryService.getTransferStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }


    /**
     * 导出 平台/商户交易分析数据 （转账）
     */
    @PostMapping("/v1/exportTransferStatistics")
    public Mono<Result<String>> exportTransferStatistics(@RequestBody @Validated
                                                             TradeStatisticsTransferReq req) {
        log.info("exportTransferStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsTransferParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsTransferParam(req);

        String exportTransferStatistics = snapshotTradeStatisticsQueryService.exportTransferStatistics(param);
        return Mono.just(Result.ok(exportTransferStatistics));
    }


    /**
     * 商户交易快照数据 （收款、代付） - 商户首页
     */
    @PostMapping("/v1/getMerchantTradeStatistics4Index")
    public Mono<Result<TradeMerchantStatisticsDTO>> getMerchantTradeStatistics4Index(@RequestBody @Validated
                                                                                         TradeMerchantStatisticsSnapshotReq req) {
        log.info("getMerchantTradeStatistics4Index req={}", JSONUtil.toJsonStr(req));
        TradeMerchantStatisticsSnapshotParam param = snapshotTradeStatisticsConverter.convertTradeMerchantStatisticsSnapshotParam(req);

        TradeMerchantStatisticsDTO statistics4Index = snapshotTradeStatisticsQueryService.getMerchantTradeStatistics4Index(param);
        return Mono.just(Result.ok(statistics4Index));
    }


    /**
     * 管理后台首页
     * 实时数据
     * 查询收款代付总数、总金额、成功数量、成功金额
     */
    @PostMapping("/v1/getTradeTimelyStatistics4Index")
    public Mono<Result<TradeTimelyStatisticsIndexDTO>> getTradeTimelyStatistics4Index(@RequestBody @Validated
                                                                                      TradeTimelyStatisticsIndexReq req) {
        log.info("getTradeTimelyStatistics4Index req={}", JSONUtil.toJsonStr(req));
        TradeTimelyStatisticsIndexParam param = snapshotTradeStatisticsConverter.convertTradeTimelyStatisticsIndexParam(req);

        TradeTimelyStatisticsIndexDTO statistics4Index = snapshotTradeStatisticsQueryService.getTradeTimelyStatistics4Index(param);
        return Mono.just(Result.ok(statistics4Index));
    }


    /**
     * 分页查询代理商报表 商户平台, 管理后台
     */
    @PostMapping("/v1/pageAgentTradeStatistics")
    public Mono<PageResult<TradeStatisticsAgentDTO>> pageAgentTradeStatistics(@RequestBody @Validated
                                                                                  TradeStatisticsAgentPageReq req) {
        log.info("pageAgentTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsAgentPageParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsAgentPageParam(req);

        PageDTO<TradeStatisticsAgentDTO> pageDTO = snapshotTradeStatisticsQueryService.pageAgentTradeStatistics(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }


    /**
     * 导出代理商报表 商户平台, 管理后台
     */
    @PostMapping("/v1/exportAgentTradeStatistics")
    public Mono<Result<String>> exportAgentTradeStatistics(@RequestBody @Validated TradeStatisticsAgentPageReq req) {
        log.info("exportAgentTradeStatistics req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsAgentPageParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsAgentPageParam(req);

        String exportAgentTradeStatistics = snapshotTradeStatisticsQueryService.exportAgentTradeStatistics(param);
        return Mono.just(Result.ok(exportAgentTradeStatistics));
    }


    /**
     * 通过代理商 分页查询商户报表 商户平台, 管理后台
     */
    @PostMapping("/v1/pageMerchantTradeStatisticsByAgent")
    public Mono<PageResult<TradeMerchantStatisticsByAgentDTO>> pageMerchantTradeStatisticsByAgent(@RequestBody @Validated
                                                                                                  TradeStatisticsByAgentPageReq req) {
        log.info("pageMerchantTradeStatisticsByAgent req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsByAgentPageParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsByAgentPageParam(req);

        PageDTO<TradeMerchantStatisticsByAgentDTO> pageDTO = snapshotTradeStatisticsQueryService.pageMerchantTradeStatisticsByAgent(param);
        return Mono.just(PageResult.ok(pageDTO.getTotal(), pageDTO.getCurrent(), pageDTO.getData()));
    }

    /**
     * 导出代理商报表 商户平台, 管理后台
     */
    @PostMapping("/v1/exportMerchantTradeStatisticsByAgent")
    public Mono<Result<String>> exportMerchantTradeStatisticsByAgent(@RequestBody @Validated TradeStatisticsByAgentPageReq req) {
        log.info("exportMerchantTradeStatisticsByAgent req={}", JSONUtil.toJsonStr(req));
        TradeStatisticsByAgentPageParam param = snapshotTradeStatisticsConverter.convertTradeStatisticsByAgentPageParam(req);

        String exportResult = snapshotTradeStatisticsQueryService.exportMerchantTradeStatisticsByAgent(param);
        return Mono.just(Result.ok(exportResult));
    }


}
