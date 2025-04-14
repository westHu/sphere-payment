package app.sphere.command.impl;

import app.sphere.command.MerchantCmdService;
import app.sphere.command.cmd.*;
import app.sphere.command.dto.MerchantAttributeDTO;
import app.sphere.manager.OrderNoManager;
import cn.hutool.core.lang.Assert;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.MerchantOperatorRepository;
import domain.sphere.repository.MerchantRepository;
import infrastructure.sphere.db.entity.Merchant;
import infrastructure.sphere.db.entity.MerchantOperator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import share.sphere.enums.MerchantStatusEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;

import java.util.Objects;
import java.util.Optional;

import static share.sphere.TradeConstant.LIMIT_1;


@Slf4j
@Service
public class MerchantCmdServiceImpl implements MerchantCmdService {

    @Resource
    MerchantRepository merchantRepository;
    @Resource
    MerchantOperatorRepository merchantOperatorRepository;
    @Resource
    OrderNoManager orderNoManager;
    @Resource
    BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addMerchant(MerchantAddCommand command) {
        log.info("addMerchant command={}", JSONUtil.toJsonStr(command));

        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantName, command.getMerchantName()).last(LIMIT_1);
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        Assert.isNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_HAS_EXIST, command.getMerchantName()));

        QueryWrapper<MerchantOperator> operatorQuery = new QueryWrapper<>();
        operatorQuery.lambda().eq(MerchantOperator::getUsername, command.getUsername()).last(LIMIT_1);
        MerchantOperator merchantOperator = merchantOperatorRepository.getOne(operatorQuery);
        Assert.isNull(merchantOperator, () -> new PaymentException(ExceptionCode.MERCHANT_HAS_EXIST, command.getUsername()));

        MerchantAttributeDTO attribute = new MerchantAttributeDTO();
        attribute.setAddBy(command.getOperator());

        merchant = new Merchant();
        merchant.setMerchantId(command.getMerchantName());
        merchant.setMerchantName(command.getMerchantName());
        merchant.setBrandName(command.getBrandName());
        merchant.setMerchantType(command.getMerchantType());
        merchant.setMerchantLevel(0);
        merchant.setApiMode(command.getApiMode());
        merchant.setCurrencyList(command.getCurrencyList());
        merchant.setTags(command.getTags());
        merchant.setStatus(MerchantStatusEnum.PENDING.getCode());
        merchant.setAttribute(JSONUtil.toJsonStr(attribute));
        merchantRepository.save(merchant);
        String merchantId = orderNoManager.getMerchantId(merchant.getId());
        merchant.setMerchantId(merchantId);
        merchantRepository.updateById(merchant);

        String password = passwordEncoder.encode(MD5.create().digestHex16(command.getPassword()));
        merchantOperator = new MerchantOperator();
        merchantOperator.setMerchantId(merchant.getMerchantId());
        merchantOperator.setMerchantName(merchant.getMerchantName());
        merchantOperator.setUsername(command.getUsername());
        merchantOperator.setPassword(password);
        merchantOperator.setLastPasswordUpdateTime(System.currentTimeMillis());
        merchantOperator.setStatus(true);
        return merchantOperatorRepository.save(merchantOperator);
    }

    @Override
    public boolean verifyMerchant(MerchantVerifyCommand command) {
        log.info("verifyMerchant command={}", JSONUtil.toJsonStr(command));

        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, command.getMerchantId()).last(LIMIT_1);
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, command.getMerchantId()));

        boolean sameOne = Optional.of(merchant).map(Merchant::getAttribute)
                .map(e -> JSONUtil.toBean(e, MerchantAttributeDTO.class))
                .map(MerchantAttributeDTO::getAddBy)
                .map(e -> e.equals(command.getOperator()))
                .orElse(false);
        Assert.isFalse(sameOne, () -> new PaymentException(ExceptionCode.SYSTEM_ERROR, command.getOperator())); //FIXME 审核不能同人

        merchant.setStatus(MerchantStatusEnum.NORMAL.getCode());
        return merchantRepository.updateById(merchant);
    }

    /**
     * 更新商户状态
     */
    @Override
    public boolean updateMerchant(MerchantUpdateCommand command) {
        log.info("updateMerchant command={}", JSONUtil.toJsonStr(command));
        String merchantId = command.getMerchantId();

        QueryWrapper<Merchant> merchantQuery = new QueryWrapper<>();
        merchantQuery.lambda().eq(Merchant::getMerchantId, merchantId);
        Merchant merchant = merchantRepository.getOne(merchantQuery);
        Assert.notNull(merchant, () -> new PaymentException(ExceptionCode.MERCHANT_NOT_FOUND, merchantId));

        UpdateWrapper<Merchant> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda()
                .set(Objects.nonNull(command.getBrandName()), Merchant::getBrandName, command.getBrandName())
                .set(Objects.nonNull(command.getMerchantType()), Merchant::getMerchantType, command.getMerchantType())
                .set(Objects.nonNull(command.getApiMode()), Merchant::getApiMode, command.getApiMode())
                .set(Objects.nonNull(command.getCurrencyList()), Merchant::getCurrencyList, command.getCurrencyList())
                .set(Objects.nonNull(command.getTags()), Merchant::getTags, command.getTags())
                .set(Objects.nonNull(command.getStatus()), Merchant::getStatus, command.getStatus())
                .eq(Merchant::getId, merchant.getId());
        return merchantRepository.update(updateWrapper);
    }

}
