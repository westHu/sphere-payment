package app.sphere.command.impl;


import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import cn.hutool.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import app.sphere.command.MerchantLoginCmdService;
import app.sphere.command.cmd.MerchantLoginCmd;
import app.sphere.command.cmd.MerchantPasswordChannelCmd;
import app.sphere.command.cmd.MerchantPasswordForgetCmd;
import app.sphere.command.cmd.MerchantPasswordResetCmd;
import app.sphere.command.cmd.MerchantSetGoogleCodeCmd;
import app.sphere.command.cmd.MerchantShowGoogleCodeCmd;
import app.sphere.command.cmd.MerchantUnsetGoogleCodeCmd;
import app.sphere.command.cmd.MerchantVerifyGoogleCodeCmd;
import app.sphere.command.dto.MerchantLoginDTO;
import infrastructure.sphere.config.GoogleAuthenticator;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantOperator;
import org.springframework.beans.BeanUtils;
import share.sphere.enums.QuerySourceEnum;
import share.sphere.enums.MerchantStatusEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import domain.sphere.repository.MerchantOperatorRepository;
import domain.sphere.repository.MerchantRepository;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.utils.JWTUtil;
import share.sphere.utils.dto.JWTTokenMerchantDTO;
import share.sphere.utils.dto.JWTTokenOperatorDTO;

import java.time.LocalDateTime;
import java.util.Optional;

import static share.sphere.TradeConstant.LIMIT_1;

@Slf4j
@Service
public class MerchantLoginCmdServiceImpl implements MerchantLoginCmdService {

    @Resource
    MerchantRepository merchantRepository;
    @Resource
    MerchantOperatorRepository merchantOperatorRepository;
    @Resource
    BCryptPasswordEncoder passwordEncoder;

    @Override
    public MerchantLoginDTO merchantLogin(MerchantLoginCmd command) {
        log.info("merchantLogin command={}", JSONUtil.toJsonStr(command));

        // 执行登录
        String username = command.getUsername();
        String password = command.getPassword();

        //校验商户操作员是否存在
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, username);
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));
        Assert.isTrue(operator.isStatus(), () -> new PaymentException(ExceptionCode.MERCHANT_DISABLED, username));

        //校验商户是否存在
        QueryWrapper<Merchant> query = new QueryWrapper<>();
        query.lambda().eq(Merchant::getMerchantId, operator.getMerchantId());
        Merchant merchant = merchantRepository.getOne(query);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));
        MerchantStatusEnum merchantStatusEnum = MerchantStatusEnum.codeToEnum(merchant.getStatus());
        Assert.equals(MerchantStatusEnum.NORMAL, merchantStatusEnum, () -> new PaymentException(ExceptionCode.MERCHANT_DISABLED, username));

        //校对密码
        boolean matches = passwordEncoder.matches(MD5.create().digestHex16(password), operator.getPassword());
        Assert.isTrue(matches, () -> new PaymentException(ExceptionCode.MERCHANT_LOGIN_ERROR2, command.getUsername()));

        //token
        JWTTokenMerchantDTO jwtTokenMerchantDTO = new JWTTokenMerchantDTO();
        BeanUtils.copyProperties(merchant, jwtTokenMerchantDTO);
        JWTTokenOperatorDTO jwtTokenOperatorDTO = new JWTTokenOperatorDTO();
        BeanUtils.copyProperties(operator, jwtTokenOperatorDTO);
        String accessToken = JWTUtil.createToken(jwtTokenMerchantDTO, jwtTokenOperatorDTO, null);

        MerchantLoginDTO merchantLoginDTO = new MerchantLoginDTO();
        merchantLoginDTO.setAccessToken(accessToken);
        merchantLoginDTO.setMerchant(merchant);
        merchantLoginDTO.setMerchantOperator(operator);
        return merchantLoginDTO;
    }

    @Override
    public boolean verifyGoogleCode(MerchantVerifyGoogleCodeCmd command) {
        log.info("verifyGoogleCode command={}", JSONUtil.toJsonStr(command));

        String username = command.getUsername();
        String authCode = command.getAuthCode();

        //查询操作员信息
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, username).last(LIMIT_1);
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        //校验谷歌验证码
        String loginAuth = Optional.of(operator).map(MerchantOperator::getGoogleCode)
                .orElseThrow(() -> new PaymentException(ExceptionCode.MERCHANT_LOGIN_ERROR3, username));
        boolean verifyCode = GoogleAuthenticator.verifyCode(authCode, loginAuth);
        Assert.isTrue(verifyCode, () -> new PaymentException(ExceptionCode.MERCHANT_LOGIN_ERROR3, username));
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
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        //校验商户是否存在
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, operator.getMerchantId());
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        // TODO N小时内更改密码一次

        //变更密码
        password = passwordEncoder.encode(MD5.create().digestHex16(password));
        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(MerchantOperator::getPassword, password)
                .set(MerchantOperator::getLastPasswordUpdateTime, LocalDateTime.now())
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorRepository.update(updateWrapper);
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
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        //校验原密码
        boolean matches = passwordEncoder.matches(MD5.create().digestHex16(oldPassword), operator.getPassword());
        Assert.isTrue(matches, () -> new PaymentException(ExceptionCode.MERCHANT_LOGIN_ERROR4, username));

        //变更密码
        newPassword = passwordEncoder.encode(MD5.create().digestHex16(newPassword));
        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(MerchantOperator::getPassword, newPassword)
                .set(MerchantOperator::getLastPasswordUpdateTime, LocalDateTime.now())
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorRepository.update(updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean resetPassword(MerchantPasswordResetCmd command) {
        log.info("resetPassword command={}", JSONUtil.toJsonStr(command));

        String username = command.getUsername();

        //校验商户操作员是否存在
        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, username);
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        //校验商户是否存在
        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, operator.getMerchantId());
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        //变更密码
        String newPassword = "123456";
        newPassword = passwordEncoder.encode(MD5.create().digestHex16(newPassword));
        UpdateWrapper<MerchantOperator> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(MerchantOperator::getPassword, newPassword)
                .eq(MerchantOperator::getId, operator.getId());
        merchantOperatorRepository.update(updateWrapper);
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
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

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

        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getMerchantId, merchantId)
                .eq(MerchantOperator::getUsername, username)
                .last(LIMIT_1);
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        //验证
        Boolean verifyCode = GoogleAuthenticator.verifyCode(authCode, loginAuth);
        Assert.isTrue(verifyCode, () -> new PaymentException(ExceptionCode.MERCHANT_LOGIN_ERROR3, username));

        //更新
        UpdateWrapper<MerchantOperator> operatorUpdate = new UpdateWrapper<>();
        operatorUpdate.lambda().set(MerchantOperator::getGoogleCode, loginAuth)
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorRepository.update(operatorUpdate);
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
        MerchantOperator operator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.notNull(operator, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, username));

        //验证
        QuerySourceEnum querySourceEnum = QuerySourceEnum.codeToEnum(querySource);
        if (QuerySourceEnum.MERCHANT_ADMIN.equals(querySourceEnum)) {
            boolean verifyCode = GoogleAuthenticator.verifyCode(authCode, operator.getGoogleCode());
            Assert.isTrue(verifyCode, () -> new PaymentException(ExceptionCode.MERCHANT_LOGIN_ERROR3, username));
        }

        //更新
        UpdateWrapper<MerchantOperator> operatorUpdate = new UpdateWrapper<>();
        operatorUpdate.lambda().set(MerchantOperator::getGoogleCode, null)
                .eq(MerchantOperator::getId, operator.getId());
        return merchantOperatorRepository.update(operatorUpdate);
    }

    //--------------------------------------------------------------------------------------------------------------

}
