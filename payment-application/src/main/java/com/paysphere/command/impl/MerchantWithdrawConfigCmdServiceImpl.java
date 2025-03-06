package com.paysphere.command.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.command.MerchantWithdrawConfigCmdService;
import com.paysphere.command.cmd.MerchantWithdrawCommand;
import com.paysphere.command.cmd.MerchantWithdrawConfigUpdateCommand;
import com.paysphere.db.entity.Merchant;
import com.paysphere.enums.MerchantStatusEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.MerchantService;
import com.paysphere.repository.MerchantWithdrawConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantWithdrawConfigCmdServiceImpl implements MerchantWithdrawConfigCmdService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantWithdrawConfigService merchantWithdrawConfigService;

    @Override
    public boolean updateMerchantWithdrawConfig(MerchantWithdrawConfigUpdateCommand command) {
        log.info("updateMerchantWithdrawConfig command={}", JSONUtil.toJsonStr(command));
        return false;
    }


    @Override
    public boolean withdraw(MerchantWithdrawCommand command) {
        log.info("withdraw command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();
        String merchantName = command.getMerchantName();
        String fromAccountNo = command.getFromAccountNo();
        BigDecimal withdrawAmount = command.getAmount();

        //查询商户
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, merchantId).last(LIMIT_1);
        Merchant merchant = merchantService.getOne(merchantQuery);
        if (Objects.isNull(merchant)) {
            log.error("withdraw merchant not exist. merchantId={}", merchantId);
            throw new PaymentException(ExceptionCode.MERCHANT_NOT_EXIST, merchantId);
        }

        //校验状态
        MerchantStatusEnum merchantStatusEnum = MerchantStatusEnum.codeToEnum(merchant.getStatus());
        if (!MerchantStatusEnum.NORMAL.equals(merchantStatusEnum)) {
            log.error("withdraw merchant status illegal. merchantId={}", merchantId);
            throw new PaymentException(ExceptionCode.MERCHANT_STATUS_ILLEGAL, merchantId);
        }


        return true;
    }


}
