package app.sphere.query.impl;

import app.sphere.query.param.SettleAccountSnapshotParam;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import infrastructure.sphere.db.entity.SettleAccount;
import infrastructure.sphere.db.entity.SettleAccountSnapshot;
import app.sphere.query.SettleAccountSnapshotQueryService;
import app.sphere.query.dto.AccountBalanceSnapshotDTO;
import app.sphere.query.dto.AccountSnapshotDTO;
import domain.sphere.repository.SettleAccountRepository;
import domain.sphere.repository.SettleAccountSnapshotRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SettleAccountSnapshotQueryServiceImpl implements SettleAccountSnapshotQueryService {

    @Resource
    SettleAccountRepository accountService;
    @Resource
    SettleAccountSnapshotRepository accountSnapshotService;

    @Override
    public AccountSnapshotDTO getAccountSnapshot(SettleAccountSnapshotParam param) {
        log.info("getAccountSnapshot param={}", JSONUtil.toJsonStr(param));

        AccountSnapshotDTO accountSnapshotDTO = new AccountSnapshotDTO();
        //余额
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda().eq(SettleAccount::getMerchantId, param.getMerchantId());
        List<SettleAccount> accountList = accountService.list(accountQuery);
        if (CollectionUtils.isNotEmpty(accountList)) {
            SettleAccount settleAccount = accountList.get(0);
            accountSnapshotDTO.setCurrency(settleAccount.getCurrency());
            accountSnapshotDTO.setAvailableBalance(settleAccount.getAvailableBalance());
            accountSnapshotDTO.setFrozenBalance(settleAccount.getFrozenBalance());
            accountSnapshotDTO.setToSettleBalance(settleAccount.getToSettleBalance());
        }


        //计算日期之间的日期
        LocalDate start = LocalDate.parse(param.getStartDate());
        LocalDate end = LocalDate.parse(param.getEndDate());
        List<String> dayList = new ArrayList<>();
        while (start.isBefore(end)) {
            dayList.add(start.toString());
            start = start.plusDays(1);
        }
        dayList.add(param.getEndDate());
        log.info("dayList={}", dayList);

        //余额快照
        QueryWrapper<SettleAccountSnapshot> snapshotQuery = new QueryWrapper<>();
        snapshotQuery.select("merchant_id as merchantId, " +
                "available_balance as availableBalance, " +
                "account_date as accountDate");
        snapshotQuery.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleAccountSnapshot::getMerchantId,
                        param.getMerchantId())
                .ge(SettleAccountSnapshot::getAccountDate, param.getStartDate())
                .le(SettleAccountSnapshot::getAccountDate, param.getEndDate())
                .groupBy(SettleAccountSnapshot::getAccountDate);
        List<SettleAccountSnapshot> snapshotList = accountSnapshotService.list(snapshotQuery);
        if (CollectionUtils.isEmpty(snapshotList)) {
            List<AccountBalanceSnapshotDTO> snapshotDTOList = dayList.stream().map(e -> {
                AccountBalanceSnapshotDTO snapshotDTO = new AccountBalanceSnapshotDTO();
                snapshotDTO.setAccountDate(e);
                snapshotDTO.setAvailableBalance(BigDecimal.ZERO);
                return snapshotDTO;
            }).collect(Collectors.toList());
            accountSnapshotDTO.setRecentAccountBalanceList(snapshotDTOList);

        } else {
            List<AccountBalanceSnapshotDTO> snapshotDTOList = snapshotList.stream().map(e -> {
                AccountBalanceSnapshotDTO snapshotDTO = new AccountBalanceSnapshotDTO();
                snapshotDTO.setAccountDate(e.getAccountDate().toString());
                snapshotDTO.setAvailableBalance(e.getAvailableBalance());
                return snapshotDTO;
            }).toList();

            List<AccountBalanceSnapshotDTO> collect = dayList.stream().map(e -> {
                AccountBalanceSnapshotDTO snapshotDTO = snapshotDTOList.stream()
                        .filter(f -> f.getAccountDate().equals(e))
                        .findAny()
                        .orElse(null);
                if (Objects.isNull(snapshotDTO)) {
                    snapshotDTO = new AccountBalanceSnapshotDTO();
                    snapshotDTO.setAccountDate(e);
                    snapshotDTO.setAvailableBalance(BigDecimal.ZERO);
                    return snapshotDTO;
                }
                return snapshotDTO;
            }).collect(Collectors.toList());
            accountSnapshotDTO.setRecentAccountBalanceList(collect);
        }

        return accountSnapshotDTO;
    }


}
