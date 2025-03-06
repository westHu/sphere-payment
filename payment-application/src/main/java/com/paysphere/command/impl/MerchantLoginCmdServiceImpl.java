package com.paysphere.command.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.command.MerchantLoginCmdService;
import com.paysphere.command.cmd.MerchantLoginCmd;
import com.paysphere.command.cmd.MerchantPasswordChannelCmd;
import com.paysphere.command.cmd.MerchantPasswordForgetCmd;
import com.paysphere.command.cmd.MerchantPasswordResetCmd;
import com.paysphere.command.cmd.MerchantSetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantShowGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import com.paysphere.command.cmd.MerchantVerifyGoogleCodeCmd;
import com.paysphere.command.dto.LoginDTO;
import com.paysphere.config.GoogleAuthenticator;
import com.paysphere.db.entity.Merchant;
import com.paysphere.db.entity.MerchantConfig;
import com.paysphere.db.entity.MerchantOperator;
import com.paysphere.enums.LoginModeEnum;
import com.paysphere.enums.MerchantQuerySourceEnum;
import com.paysphere.enums.MerchantStatusEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.repository.MerchantConfigService;
import com.paysphere.repository.MerchantOperatorService;
import com.paysphere.repository.MerchantService;
import com.paysphere.utils.Md5Util;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.paysphere.TradeConstant.LIMIT_1;
import static com.paysphere.exception.ExceptionCode.CAPTCHA_NOT_EXIST;
import static com.paysphere.exception.ExceptionCode.CAPTCHA_VERIFY_FAILED;
import static com.paysphere.exception.ExceptionCode.LOGIN_OTP_AUTH_ERROR;
import static com.paysphere.exception.ExceptionCode.MERCHANT_NOT_CONFIRM;
import static com.paysphere.exception.ExceptionCode.MERCHANT_NOT_EXIST;
import static com.paysphere.exception.ExceptionCode.MERCHANT_STATUS_CANCEL;
import static com.paysphere.exception.ExceptionCode.MERCHANT_STATUS_DORMANT;
import static com.paysphere.exception.ExceptionCode.MERCHANT_STATUS_FROZEN;
import static com.paysphere.exception.ExceptionCode.OPERATOR_NOT_EXIST;
import static com.paysphere.exception.ExceptionCode.USERNAME_PASSWORD_INCORRECT;

@Slf4j
@Service
public class MerchantLoginCmdServiceImpl implements MerchantLoginCmdService {

    @Resource
    MerchantService merchantService;
    @Resource
    MerchantOperatorService merchantOperatorService;
    @Resource
    MerchantConfigService merchantConfigService;
    @Resource
    BCryptPasswordEncoder passwordEncoder;


    @Override
    public LoginDTO merchantLogin(MerchantLoginCmd command) {
        log.info("merchantLogin command={}", JSONUtil.toJsonStr(command));
        throw new PaymentException("unsupported operator role");
    }


    @Override
    public boolean verifyGoogleCode(MerchantVerifyGoogleCodeCmd command) {
        log.info("verifyGoogleCode command={}", JSONUtil.toJsonStr(command));

        String username = command.getUsername();
        String authCode = command.getAuthCode();

        //查询操作员信息
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, username).last(LIMIT_1);
        MerchantOperator operator = merchantOperatorService.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(OPERATOR_NOT_EXIST, username));

        //校验谷歌验证码
        String loginAuth = Optional.of(operator).map(MerchantOperator::getGoogleCode)
                .orElseThrow(() -> new PaymentException(CAPTCHA_NOT_EXIST));
        Boolean verifyCode = GoogleAuthenticator.verifyCode(authCode, loginAuth);
        Assert.isTrue(verifyCode, () -> new PaymentException(CAPTCHA_VERIFY_FAILED, username));
        return true;
    }

    @Override
    public boolean forgetPassword(MerchantPasswordForgetCmd command) {
        log.info("forgetPassword command={}", JSONUtil.toJsonStr(command));

        String username = command.getUsername();
        String password = command.getPassword();

        //校验商户操作员是否存在
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, username);
        MerchantOperator operator = merchantOperatorService.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(OPERATOR_NOT_EXIST, username));

        //校验商户是否存在
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, operator.getMerchantId());
        Merchant merchant = merchantService.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(MERCHANT_NOT_EXIST, username));

        // N小时内更改密码一次

        //变更密码
        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(MerchantOperator::getPassword,
                        passwordEncoder.encode(Md5Util.getMD5Code(password)))
                .set(MerchantOperator::getLastPasswordUpdateTime, LocalDateTime.now())
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorService.update(updateWrapper);
    }


    @Override
    public boolean changePassword(MerchantPasswordChannelCmd command) {
        log.info("changePassword command={}", JSONUtil.toJsonStr(command));

        String username = command.getUsername();
        String oldPassword = command.getOldPassword();
        String newPassword = command.getNewPassword();

        //校验商户操作员是否存在
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, username);
        MerchantOperator operator = merchantOperatorService.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(OPERATOR_NOT_EXIST, username));

        //校验原密码
        boolean matches = passwordEncoder.matches(Md5Util.getMD5Code(oldPassword), operator.getPassword());
        Assert.isTrue(matches, () -> new PaymentException(ExceptionCode.OLD_PASSWORD_IS_WRONG, username));

        //变更密码
        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(MerchantOperator::getPassword,
                        passwordEncoder.encode(Md5Util.getMD5Code(newPassword)))
                .set(MerchantOperator::getLastPasswordUpdateTime, LocalDateTime.now())
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorService.update(updateWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(MerchantPasswordResetCmd command) {
        log.info("resetPassword command={}", JSONUtil.toJsonStr(command));

        String username = command.getUsername();

        //校验商户操作员是否存在
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, username);
        MerchantOperator operator = merchantOperatorService.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(OPERATOR_NOT_EXIST, username));

        //校验商户是否存在
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, operator.getMerchantId());
        Merchant merchant = merchantService.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(MERCHANT_NOT_EXIST, username));

        //变更密码
        String newPassword = "123456";
        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(MerchantOperator::getPassword,
                        passwordEncoder.encode(Md5Util.getMD5Code(newPassword)))
                .eq(MerchantOperator::getId, operator.getId());
        merchantOperatorService.update(updateWrapper);
        return true;
    }


    @Override
    public String showGoogleAuth(MerchantShowGoogleCodeCmd command) {
        log.info("showGoogleAuth command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();
        String username = command.getUsername();

        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getMerchantId, merchantId)
                .eq(MerchantOperator::getUsername, username)
                .last(LIMIT_1);
        MerchantOperator operator = merchantOperatorService.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(OPERATOR_NOT_EXIST, username));

        return Optional.of(operator)
                .map(MerchantOperator::getGoogleCode)
                .map(GoogleAuthenticator::genURL)
                .orElseGet(GoogleAuthenticator::genURL);
    }


    @Override
    public boolean setGoogleCode(MerchantSetGoogleCodeCmd command) {
        log.info("setGoogleCode command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();
        String username = command.getUsername();
        String loginAuth = command.getLoginAuth();
        String authCode = command.getAuthCode();

        //验证
        Boolean verifyCode = GoogleAuthenticator.verifyCode(authCode, loginAuth);
        Assert.isTrue(verifyCode, () -> new PaymentException(LOGIN_OTP_AUTH_ERROR, username));

        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getMerchantId, merchantId)
                .eq(MerchantOperator::getUsername, username)
                .last(LIMIT_1);
        MerchantOperator operator = merchantOperatorService.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(OPERATOR_NOT_EXIST, username));

        //更新
        UpdateWrapper<MerchantOperator> operatorUpdate = new UpdateWrapper<>();
        operatorUpdate.lambda().set(MerchantOperator::getGoogleCode, loginAuth)
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorService.update(operatorUpdate);
    }


    @Override
    public boolean unsetGoogleAuth(MerchantUnsetGoogleCodeCmd command) {
        log.info("unsetGoogleAuth command={}", JSONUtil.toJsonStr(command));

        String merchantId = command.getMerchantId();
        String username = command.getUsername();
        String authCode = command.getAuthCode();
        Integer querySource = command.getQuerySource();

        //查询
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getMerchantId, merchantId)
                .eq(MerchantOperator::getUsername, username)
                .last(LIMIT_1);
        MerchantOperator operator = merchantOperatorService.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(OPERATOR_NOT_EXIST, username));

        //验证
        MerchantQuerySourceEnum querySourceEnum = MerchantQuerySourceEnum.codeToEnum(querySource);
        if (MerchantQuerySourceEnum.MERCHANT_ADMIN.equals(querySourceEnum)) {
            Boolean verifyCode = GoogleAuthenticator.verifyCode(authCode, operator.getGoogleCode());
            Assert.isTrue(verifyCode, () -> new PaymentException(LOGIN_OTP_AUTH_ERROR, username));
        }

        //更新
        UpdateWrapper<MerchantOperator> operatorUpdate = new UpdateWrapper<>();
        operatorUpdate.lambda().set(MerchantOperator::getGoogleCode, null)
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorService.update(operatorUpdate);
    }


    //--------------------------------------------------------------------------------------------------------------


    /**
     * 商户登录
     */
    private LoginDTO handleMerchantLogin(MerchantLoginCmd command) {
        LoginModeEnum modeEnum = LoginModeEnum.codeToEnum(command.getMode());

        //密码模式 & 谷歌模式
        LoginDTO loginDTO;
        if (LoginModeEnum.PASSWORD.equals(modeEnum)) {
            loginDTO = loginByPassword(command);
        } else {
            throw new PaymentException(ExceptionCode.UNSUPPORTED_LOGIN_TYPE);
        }

        //查询商户配置: 业务作用
        String merchantId = loginDTO.getMerchant().getMerchantId();
        QueryWrapper<MerchantConfig> configQuery = new QueryWrapper<>();
        configQuery.lambda().eq(MerchantConfig::getMerchantId, merchantId).last(LIMIT_1);
        MerchantConfig merchantConfig = merchantConfigService.getOne(configQuery);
        loginDTO.setMerchantConfig(merchantConfig);
        return loginDTO;
    }


    /**
     * 账户密码登录
     */
    private LoginDTO loginByPassword(MerchantLoginCmd command) {
        String username = command.getUsername();
        String password = command.getPassword();

        Merchant merchant;
        MerchantOperator operator;

        //校验商户操作员是否存在
        QueryWrapper<MerchantOperator> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(MerchantOperator::getUsername, username);
        operator = merchantOperatorService.getOne(queryWrapper);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.USERNAME_NOT_FOUND, username));
        Assert.isTrue(operator.isStatus(), () -> new PaymentException(ExceptionCode.USERNAME_STATUS_INVALID, username));

        //校验商户是否存在
        QueryWrapper<Merchant> query = new QueryWrapper<>();
        query.lambda().eq(Merchant::getMerchantId, operator.getMerchantId());
        merchant = merchantService.getOne(query);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.USERNAME_NOT_FOUND, username));

        //校验商户状态
        MerchantStatusEnum merchantStatusEnum = MerchantStatusEnum.codeToEnum(merchant.getStatus());
        switch (merchantStatusEnum) {
            case PENDING_ACTIVE -> throw new PaymentException(MERCHANT_NOT_CONFIRM, merchant.getMerchantName());
            case FROZEN -> throw new PaymentException(MERCHANT_STATUS_FROZEN, merchant.getMerchantName());
            case DORMANT -> throw new PaymentException(MERCHANT_STATUS_DORMANT, merchant.getMerchantName());
            case CANCEL -> throw new PaymentException(MERCHANT_STATUS_CANCEL, merchant.getMerchantName());
        }

        //校对密码
        boolean matches = passwordEncoder.matches(Md5Util.getMD5Code(password), operator.getPassword());
        Assert.isTrue(matches, () -> new PaymentException(USERNAME_PASSWORD_INCORRECT, command.getUsername()));

        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setMerchant(merchant);
        loginDTO.setMerchantOperator(operator);
        return loginDTO;
    }


}
