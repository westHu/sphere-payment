package app.sphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantWithdrawConfig;
import app.sphere.query.MerchantWithdrawConfigQueryService;
import app.sphere.query.dto.MerchantWithdrawConfigDTO;
import app.sphere.query.param.MerchantIdParam;
import domain.sphere.repository.MerchantRepository;
import domain.sphere.repository.MerchantWithdrawConfigRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantWithdrawConfigQueryServiceImpl implements MerchantWithdrawConfigQueryService {

    @Resource
    MerchantRepository merchantRepository;
    @Resource
    MerchantWithdrawConfigRepository merchantWithdrawConfigRepository;


    @Override
    public MerchantWithdrawConfigDTO getMerchantWithdrawConfig(MerchantIdParam param) {
        if (Objects.isNull(param)) {
            return null;
        }

        //查询商户
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        if (Objects.isNull(merchant)) {
            log.error("getMerchantPayoutConfig merchant is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        //查询商户代付配置
        QueryWrapper<MerchantWithdrawConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantWithdrawConfig::getMerchantId, param.getMerchantId()).last(LIMIT_1);
        MerchantWithdrawConfig withdrawConfig = merchantWithdrawConfigRepository.getOne(configQuery);
        if (Objects.isNull(withdrawConfig)) {
            log.error("getMerchantWithdrawConfig withdrawConfig is null. param={}", JSONUtil.toJsonStr(param));
            return null;
        }

        MerchantWithdrawConfigDTO withdrawConfigDTO = new MerchantWithdrawConfigDTO();
        withdrawConfigDTO.setMerchantId(withdrawConfig.getMerchantId());
        withdrawConfigDTO.setDeductionType(withdrawConfig.getDeductionType());
        return withdrawConfigDTO;
    }
}
