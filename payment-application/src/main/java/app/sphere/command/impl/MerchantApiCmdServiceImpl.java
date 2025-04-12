package app.sphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import app.sphere.command.MerchantApiCmdService;
import app.sphere.command.cmd.MerchantUpdateStatusCommand;
import infrastructure.sphere.db.entity.Merchant;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.MerchantOperatorRepository;
import domain.sphere.repository.MerchantRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class MerchantApiCmdServiceImpl implements MerchantApiCmdService {

    @Resource
    MerchantRepository merchantRepository;

    /**
     * 更新商户状态
     */
    @Override
    public boolean updateMerchantStatus(MerchantUpdateStatusCommand command) {
        log.info("updateMerchantStatus command={}", JSONUtil.toJsonStr(command));
        String merchantId = command.getMerchantId();

        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, merchantId);
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, merchantId));

        UpdateWrapper<Merchant> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(Merchant::getStatus, command.getStatus())
                .eq(Merchant::getId, merchant.getId());
        return merchantRepository.update(updateWrapper);
    }

}
