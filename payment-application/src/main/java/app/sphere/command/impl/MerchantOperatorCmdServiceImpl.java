package app.sphere.command.impl;


import app.sphere.command.MerchantOperatorCmdService;
import app.sphere.command.cmd.MerchantOperatorAddCmd;
import app.sphere.command.cmd.MerchantOperatorUpdateCmd;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.MerchantOperatorRepository;
import infrastructure.sphere.db.entity.MerchantOperator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantOperatorCmdServiceImpl implements MerchantOperatorCmdService {

    @Resource
    MerchantOperatorRepository merchantOperatorRepository;
    @Resource
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public boolean addMerchantOperator(MerchantOperatorAddCmd cmd) {
        log.info("addMerchantOperator cmd={}", JSONUtil.toJsonStr(cmd));

        String username = cmd.getUsername();
        String password = cmd.getPassword();

        //校验是否存在
        QueryWrapper<MerchantOperator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MerchantOperator::getUsername, username).last(LIMIT_1);
        MerchantOperator operator = merchantOperatorRepository.getOne(queryWrapper);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_HAS_EXIST, username));

        password = passwordEncoder.encode(MD5.create().digestHex16(password));
        MerchantOperator mOperator = new MerchantOperator();
        mOperator.setMerchantId(cmd.getMerchantId());
        mOperator.setUsername(username);
        mOperator.setPassword(password);
        mOperator.setLastPasswordUpdateTime(System.currentTimeMillis());
        mOperator.setGoogleCode(null);
        mOperator.setStatus(true);
        return merchantOperatorRepository.save(mOperator);
    }

    @Override
    public boolean updateMerchantOperator(MerchantOperatorUpdateCmd cmd) {
        log.info("updateMerchantOperator cmd={}", JSONUtil.toJsonStr(cmd));
        String password = cmd.getPassword();

        MerchantOperator operator = merchantOperatorRepository.getById(cmd.getId());
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_HAS_EXIST));

        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        if (StringUtils.isNotBlank(password)) {
            password = passwordEncoder.encode(MD5.create().digestHex16(password));
            updateWrapper.lambda().set(MerchantOperator::getPassword, password);
        }

        updateWrapper.lambda().eq(MerchantOperator::getId, cmd.getId());
        return merchantOperatorRepository.update(updateWrapper);
    }

    //==============================================================================================================

}
