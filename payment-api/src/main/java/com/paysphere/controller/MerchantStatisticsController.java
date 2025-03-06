package com.paysphere.controller;


import com.paysphere.query.MerchantStatisticsQueryService;
import com.paysphere.query.dto.MerchantTimelyStatisticsIndexDTO;
import com.paysphere.result.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;


/**
 * 商户数据分析API
 */
@Slf4j
@RestController
public class MerchantStatisticsController {


    @Resource
    MerchantStatisticsQueryService merchantStatisticsQueryService;

    /**
     * 管理平台首页
     * 实时数据
     * 商户数量
     */
    @PostMapping("/v1/getMerchantTimelyStatistics4Index")
    public Mono<Result<MerchantTimelyStatisticsIndexDTO>> getMerchantTimelyStatistics4Index() {
        log.info("getMerchantTimelyStatistics4Index time={}", LocalDateTime.now());

        MerchantTimelyStatisticsIndexDTO index = merchantStatisticsQueryService.getMerchantTimelyStatistics4Index();
        return Mono.just(Result.ok(index));
    }

}
