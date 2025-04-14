package app.sphere.query.impl;

import app.sphere.query.MerchantStatisticsQueryService;
import app.sphere.query.dto.MerchantTimelyStatisticsIndexDTO;
import cn.hutool.json.JSONUtil;
import domain.sphere.repository.MerchantRepository;
import infrastructure.sphere.db.entity.Merchant;
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
    MerchantRepository merchantRepository;

    @Override
    public MerchantTimelyStatisticsIndexDTO getMerchantTimelyStatistics4Index() {
        MerchantTimelyStatisticsIndexDTO statisticsDTO = new MerchantTimelyStatisticsIndexDTO();
        List<Merchant> merchantList = merchantRepository.list();
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
