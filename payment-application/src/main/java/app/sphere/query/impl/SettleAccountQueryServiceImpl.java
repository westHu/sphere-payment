package app.sphere.query.impl;

import app.sphere.query.SettleAccountQueryService;
import app.sphere.query.dto.SettleAccountDTO;
import app.sphere.query.dto.SettleAccountDropDTO;
import app.sphere.query.param.SettleAccountDropParam;
import app.sphere.query.param.SettleAccountListParam;
import app.sphere.query.param.SettleAccountPageParam;
import app.sphere.query.param.SettleAccountParam;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.SettleAccountRepository;
import infrastructure.sphere.db.entity.SettleAccount;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class SettleAccountQueryServiceImpl implements SettleAccountQueryService {

    @Resource
    SettleAccountRepository settleAccountRepository;

    @Override
    public List<SettleAccountDropDTO> dropSettleAccountList(SettleAccountDropParam param) {
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.select("account_no as accountNo, merchant_id as merchantId, merchant_name as merchantName");
        List<SettleAccount> accountList = settleAccountRepository.list(accountQuery);

        return accountList.stream().map(e -> {
            SettleAccountDropDTO dropDTO = new SettleAccountDropDTO();
            return dropDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public Page<SettleAccount> pageSettleAccountList(SettleAccountPageParam param) {
        if (Objects.isNull(param)) {
            return new Page<>();
        }

        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda()
                .eq(Objects.nonNull(param.getAccountType()), SettleAccount::getAccountType, param.getAccountType())
                .eq(Objects.nonNull(param.getRole()), SettleAccount::getRegion, param.getRole())
                .in(CollectionUtils.isNotEmpty(param.getAccountTypeList()), SettleAccount::getAccountType, param.getAccountTypeList())
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleAccount::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), SettleAccount::getAccountNo, param.getAccountNo())
                .orderByDesc(SettleAccount::getAvailableBalance) //1
                .orderByAsc(SettleAccount::getMerchantId); //1
        return settleAccountRepository.page(new Page<>(param.getPageNum(), param.getPageSize()), accountQuery);
    }

    @Override
    public List<SettleAccountDTO> getSettleAccountList(SettleAccountListParam param) {
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleAccount::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), SettleAccount::getAccountNo, param.getAccountNo())
                .in(CollectionUtils.isNotEmpty(param.getAreaList()), SettleAccount::getRegion, param.getAreaList());
        List<SettleAccount> accountList = settleAccountRepository.list(accountQuery);

        return accountList.stream().map(e -> {
            SettleAccountDTO dto = new SettleAccountDTO();
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public SettleAccount getSettleAccount(SettleAccountParam param) {
        QueryWrapper<SettleAccount> accountQuery = new QueryWrapper<>();
        accountQuery.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), SettleAccount::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getAccountNo()), SettleAccount::getAccountNo, param.getAccountNo())
                .last(LIMIT_1);
        return settleAccountRepository.getOne(accountQuery);
    }

}
