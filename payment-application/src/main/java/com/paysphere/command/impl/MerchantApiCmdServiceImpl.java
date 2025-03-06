package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.cache.RedisService;
import com.paysphere.command.MerchantApiCmdService;
import com.paysphere.command.cmd.MerchantUpdateStatusCommand;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.repository.MerchantOperatorService;
import com.paysphere.repository.MerchantService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.paysphere.TradeConstant.LIMIT_1;
import static com.paysphere.TradeConstant.LOCK_NAME_EMAIL_CODE;


@Slf4j
@Service
public class MerchantApiCmdServiceImpl implements MerchantApiCmdService {

    @Resource
    MerchantService merchantService;
    @Resource
    RedisService redisService;
    @Resource
    MerchantOperatorService merchantOperatorService;
    @Resource
    RocketMqProducer rocketMqProducer;

    /**
     * 通过邮件发送验证码
     */

    @Override
    public boolean sendCode2Email(String email) {
        log.info("sendCode2Email email={}", email);

        //校验邮箱是否存在
        QueryWrapper<MerchantOperator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MerchantOperator::getUsername, email).last(LIMIT_1);
        MerchantOperator operator = merchantOperatorService.getOne(queryWrapper);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.OPERATOR_NOT_EXIST, email));

        //随机6位数字验证码
        String code = RandomStringUtils.randomNumeric(6).toLowerCase();

        //TODO 发送邮件

        //数字存入Redis
        String key = LOCK_NAME_EMAIL_CODE + code;
        log.info("sendCode2Email key={}, email={}", key, email);
        return redisService.set(key, email, 15L * 60L);
    }


    /**
     * 验证邮件验证码
     */
    @Override
    public boolean verifyCode4Email(String email, String code) {
        log.info("verifyCode4Email param={} {}", email, code);

        //校验邮箱是否存在
        QueryWrapper<MerchantOperator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MerchantOperator::getUsername, email).last(LIMIT_1);
        MerchantOperator operator = merchantOperatorService.getOne(queryWrapper);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.OPERATOR_NOT_EXIST, email));

        //redis取出数字验证码
        String key = LOCK_NAME_EMAIL_CODE + code;
        Object value = redisService.get(key);
        log.info("sendCode2Email key={}, value={}", key, value);

        if (Objects.isNull(value) || !email.equals(value)) {
            throw new PaymentException(ExceptionCode.EMAIL_CODE_VERIFY_EXCEPTION, email);
        }

        //Redis删除验证码
        redisService.expire(key, 1);
        return true;
    }


    /**
     * 更新商户状态
     */

    @Override
    public boolean updateMerchantStatus(MerchantUpdateStatusCommand command) {
        log.info("updateMerchantStatus command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();

        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, merchantId);
        Merchant merchant = merchantService.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_EXIST, merchantId));

        UpdateWrapper<Merchant> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(Merchant::getStatus, command.getStatus())
                .eq(Merchant::getId, merchant.getId());
        return merchantService.update(updateWrapper);
    }

}
