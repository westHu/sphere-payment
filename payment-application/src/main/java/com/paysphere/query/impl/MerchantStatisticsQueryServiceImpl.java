package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.paysphere.db.entity.Merchant;
import com.paysphere.query.MerchantStatisticsQueryService;
import com.paysphere.query.dto.MerchantTimelyStatisticsIndexDTO;
import com.paysphere.repository.MerchantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class MerchantStatisticsQueryServiceImpl implements MerchantStatisticsQueryService {

    @Resource
    MerchantService merchantService;


    @Override
    public MerchantTimelyStatisticsIndexDTO getMerchantTimelyStatistics4Index() {
        MerchantTimelyStatisticsIndexDTO statisticsDTO = new MerchantTimelyStatisticsIndexDTO();
        List<Merchant> merchantList = merchantService.list();
        log.info("getMerchantTimelyStatistics4Index merchantList={}", JSONUtil.toJsonStr(merchantList));
        if (CollectionUtils.isEmpty(merchantList)) {
            return statisticsDTO;
        }
        LocalDate date1 = LocalDate.now().minusDays(1);
        LocalDate date7 = LocalDate.now().minusDays(7);

        int size = merchantList.size();
        long count1 = merchantList.stream()
                .filter(e -> Objects.nonNull(e.getCreateTime()) && e.getCreateTime().isAfter(date1.atStartOfDay()))
                .count();
        long count7 = merchantList.stream()
                .filter(e -> Objects.nonNull(e.getCreateTime()) && e.getCreateTime().isAfter(date7.atStartOfDay()))
                .count();

        statisticsDTO.setMerchantCount(size);
        statisticsDTO.setMerchantCount1Add((int) count1);
        statisticsDTO.setMerchantCount7Add((int) count7);
        return statisticsDTO;
    }
}
