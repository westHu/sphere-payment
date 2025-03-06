package com.paysphere.command.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.command.MerchantOperatorCmdService;
import com.paysphere.command.cmd.MerchantOperatorAddCmd;
import com.paysphere.command.cmd.MerchantOperatorUpdateCmd;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.MerchantOperatorService;
import com.paysphere.utils.Md5Util;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static com.paysphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantOperatorCmdServiceImpl implements MerchantOperatorCmdService {

    @Resource
    MerchantOperatorService merchantOperatorService;
    @Resource
    BCryptPasswordEncoder passwordEncoder;


    @Override
    public boolean addMerchantOperator(MerchantOperatorAddCmd cmd) {
        log.info("addMerchantOperator cmd={}", JSONUtil.toJsonStr(cmd));

        //校验是否存在
        String username = cmd.getUsername();
        QueryWrapper<MerchantOperator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MerchantOperator::getUsername, username).last(LIMIT_1);
        MerchantOperator one = merchantOperatorService.getOne(queryWrapper);
        if (Objects.nonNull(one)) {
            throw new PaymentException(ExceptionCode.OPERATOR_HAS_EXIST, username);
        }

        String encode = passwordEncoder.encode(cmd.getPassword());
        MerchantOperator mOperator = new MerchantOperator();
        mOperator.setMerchantId(cmd.getMerchantId());
        mOperator.setRole(cmd.getRole());
        mOperator.setUsername(encode);
        mOperator.setPassword(encode);
        mOperator.setTradePassword(cmd.getPassword());
        mOperator.setLastPasswordUpdateTime(LocalDateTime.now());
        mOperator.setLastTradePasswordUpdateTime(LocalDateTime.now());
        mOperator.setGoogleCode(null);
        mOperator.setGoogleEmail(null);
        mOperator.setStatus(true);
        return merchantOperatorService.save(mOperator);
    }


    @Override
    public boolean updateMerchantOperator(MerchantOperatorUpdateCmd cmd) {
        log.info("updateMerchantOperator cmd={}", JSONUtil.toJsonStr(cmd));

        MerchantOperator operator = merchantOperatorService.getById(cmd.getId());
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.OPERATOR_NOT_EXIST));
        if (StringUtils.isBlank(cmd.getPassword()) && Objects.isNull(cmd.getRole())) {
            return true;
        }

        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        if (StringUtils.isNotBlank(cmd.getPassword())) {
            String password = passwordEncoder.encode(Md5Util.getMD5Code(cmd.getPassword()));
            updateWrapper.lambda().set(MerchantOperator::getPassword, password);
        }

        if (Objects.nonNull(cmd.getRole())) {
            updateWrapper.lambda().set(MerchantOperator::getRole, cmd.getRole());
        }
        updateWrapper.lambda().eq(MerchantOperator::getId, cmd.getId());
        return merchantOperatorService.update(updateWrapper);
    }


    //-----------------------------------------------------------------------------------------------

}
