package com.paysphere.controller;

import cn.hutool.json.JSONUtil;
import com.paysphere.controller.request.SettleTimelyStatisticsIndexReq;
import com.paysphere.convert.SettleStatisticsConverter;
import com.paysphere.query.SettleStatisticsQueryService;
import com.paysphere.query.dto.SettleTimelyStatisticsIndexDTO;
import com.paysphere.query.param.SettleTimelyStatisticsIndexParam;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * 结算数据分析API
 */
@Slf4j
@RestController
public class SettleStatisticsController {


    @Resource
    SettleStatisticsConverter settleStatisticsConverter;
    @Resource
    SettleStatisticsQueryService settleStatisticsQueryService;

    /**
     * 管理平台首页
     * 实时数据
     * 查询商户手续费、分润、渠道成本、平台利润
     */
    @PostMapping("/v1/getSettleTimelyStatistics4Index")
    public Mono<Result<SettleTimelyStatisticsIndexDTO>> getSettleTimelyStatistics4Index(@RequestBody @Validated SettleTimelyStatisticsIndexReq req) {
        log.info("getSettleTimelyStatistics4Index req={}", JSONUtil.toJsonStr(req));
        SettleTimelyStatisticsIndexParam param = settleStatisticsConverter.convertSettleTimelyStatisticsIndexParam(req);

        SettleTimelyStatisticsIndexDTO index = settleStatisticsQueryService.getSettleTimelyStatistics4Index(param);
        return Mono.just(Result.ok(index));
    }
}
