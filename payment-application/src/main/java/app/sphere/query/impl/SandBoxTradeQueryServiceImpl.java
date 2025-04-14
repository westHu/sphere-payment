package app.sphere.query.impl;

import app.sphere.query.SandBoxTradeQueryService;
import app.sphere.query.dto.*;
import app.sphere.query.param.*;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import domain.sphere.repository.*;
import infrastructure.sphere.db.entity.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.*;
import share.sphere.exception.PaymentException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static share.sphere.TradeConstant.LIMIT_1;
import static share.sphere.TradeConstant.TRADE_EXPIRY_PERIOD_MAX;

@Slf4j
@Service
public class SandBoxTradeQueryServiceImpl implements SandBoxTradeQueryService {

    @Resource
    TradeSandboxPaymentOrderRepository tradeSandboxPaymentOrderRepository;
    @Resource
    TradeSandboxPayoutOrderRepository tradeSandboxPayoutOrderRepository;

    @Override
    public PageDTO<SandboxTradePaymentOrderPageDTO> pageSandBoxPayOrderList(SandboxTradePaymentOrderPageParam param) {
        log.info("pageSandBoxPayOrderList param={}", JSONUtil.toJsonStr(param));

        QueryWrapper<TradeSandboxPaymentOrder> payQuery = new QueryWrapper<>();
        payQuery.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeSandboxPaymentOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeSandboxPaymentOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradeSandboxPaymentOrder::getOrderNo, param.getOrderNo())
                .orderByDesc(TradeSandboxPaymentOrder::getTradeTime);
        Page<TradeSandboxPaymentOrder> page = tradeSandboxPaymentOrderRepository.page(new Page<>(param.getPageNum(),
                param.getPageSize()), payQuery);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<SandboxTradePaymentOrderPageDTO> collect = page.getRecords().stream().map(e -> {
            SandboxTradePaymentOrderPageDTO dto = new SandboxTradePaymentOrderPageDTO();
            dto.setTradeNo(e.getTradeNo());
            dto.setOrderNo(e.getOrderNo());
            dto.setPaymentMethod(e.getPaymentMethod());
            dto.setMerchantId(e.getMerchantId());
            dto.setMerchantName(e.getMerchantName());
            dto.setAmount(e.getAmount());
            dto.setMerchantFee(e.getMerchantFee());
            dto.setAccountAmount(e.getAccountAmount());
            dto.setAccountAmount(e.getAmount());
            dto.setPaymentStatus(e.getPaymentStatus());
            dto.setCallBackStatus(e.getCallBackStatus());
//            dto.setTradeTime(e.getTradeTime());
            dto.setPaymentFinishTime(System.currentTimeMillis());
            return dto;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }


    @Override
    public PageDTO<SandboxTradePayoutOrderPageDTO> pageSandboxCashOrderList(SandboxTradePayoutOrderPageParam param) {
        log.info("pageSandboxCashOrderList param={}", JSONUtil.toJsonStr(param));

        QueryWrapper<TradeSandboxPayoutOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda()
                .eq(StringUtils.isNotBlank(param.getMerchantId()), TradeSandboxPayoutOrder::getMerchantId, param.getMerchantId())
                .eq(StringUtils.isNotBlank(param.getTradeNo()), TradeSandboxPayoutOrder::getTradeNo, param.getTradeNo())
                .eq(StringUtils.isNotBlank(param.getOrderNo()), TradeSandboxPayoutOrder::getOrderNo, param.getOrderNo())
                .orderByDesc(TradeSandboxPayoutOrder::getTradeTime);
        Page<TradeSandboxPayoutOrder> page = tradeSandboxPayoutOrderRepository.page(new Page<>(param.getPageNum(),
                param.getPageSize()), queryWrapper);
        if (Objects.isNull(page) || page.getTotal() == 0) {
            return PageDTO.empty();
        }

        List<SandboxTradePayoutOrderPageDTO> collect = page.getRecords().stream().map(e -> {
            SandboxTradePayoutOrderPageDTO dto = new SandboxTradePayoutOrderPageDTO();
            dto.setTradeNo(e.getTradeNo());
            dto.setOrderNo(e.getOrderNo());
            dto.setPaymentMethod(e.getPaymentMethod());
            dto.setMerchantId(e.getMerchantId());
            dto.setMerchantName(e.getMerchantName());
            dto.setAmount(e.getAmount());
            dto.setMerchantFee(e.getMerchantFee());
            dto.setAccountAmount(e.getAmount());
            dto.setPaymentStatus(e.getPaymentStatus());
            dto.setCallBackStatus(e.getCallBackStatus());
//            dto.setTradeTime(e.getTradeTime());
//            dto.setPaymentFinishTime(e.getPaymentFinishTime());
            return dto;
        }).toList();

        return PageDTO.of(page.getTotal(), page.getCurrent(), collect);
    }

    /***
     * 沙箱 获取收银台
     */

    @Override
    public CashierDTO getSandboxCashier(CashierParam param) {
        log.info("getSandboxCashier param={}", JSONUtil.toJsonStr(param));

        String tradeNo = param.getTradeNo();
        String timestamp = param.getTimestamp();
        String token = param.getToken();

        // 校验参数
//        if (StringUtils.isNoneBlank(timestamp, token)) {
//            String cashierToken = AesUtils.cashierToken(tradeNo + timestamp);
//            if (!token.equals(cashierToken)) {
//                throw new PaymentException("Sandbox payment url. invalid token");
//            }
//        }

        // 校验订单是否存在
        QueryWrapper<TradeSandboxPaymentOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TradeSandboxPaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradeSandboxPaymentOrder order = tradeSandboxPaymentOrderRepository.getOne(queryWrapper);
        Assert.notNull(order, () -> new PaymentException("Sandbox Order not exist. " + tradeNo));

        // 构建收银台数据
        CashierDTO dto = new CashierDTO();
        dto.setTradeNo(order.getTradeNo());
        dto.setTradeTime(null);

        dto.setMerchantId(order.getMerchantId());
        dto.setMerchantName(order.getMerchantName());
        dto.setCurrency(order.getCurrency());
        dto.setAmount(order.getAmount());
        dto.setExpiryPeriod(getExpiryPeriod(null));

        TradeStatusEnum tradeStatusEnum = TradeStatusEnum.codeToEnum(order.getTradeStatus());
        switch (tradeStatusEnum) {
            case TRADE_INIT -> initPaymentMethod(dto);
            case TRADE_SUCCESS -> {
                dto.setOnMethod(buildPaymentMethod(order.getPaymentMethod()));

            }
            case TRADE_FAILED ->
                    throw new PaymentException("Sandbox Order [" + order.getTradeNo() + "] already payment failed.");
            case TRADE_EXPIRED ->
                    throw new PaymentException("Sandbox Order [" + order.getTradeNo() + "] already expired.");
            default -> throw new PaymentException("Sandbox Order [" + order.getTradeNo() + "] status error.");
        }

        log.info("getSandboxCashier dto={}", JSONUtil.toJsonStr(dto));
        return dto;
    }

    @Override
    public String getSandboxMerchantStep(String merchantId) {
        // 查询收款
        QueryWrapper<TradeSandboxPaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradeSandboxPaymentOrder::getMerchantId, merchantId)
                .eq(TradeSandboxPaymentOrder::getCallBackStatus, 1)
                .last(LIMIT_1);
        TradeSandboxPaymentOrder payOrder = tradeSandboxPaymentOrderRepository.getOne(payOrderQuery);

        // 查询代付
        QueryWrapper<TradeSandboxPayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradeSandboxPayoutOrder::getMerchantId, merchantId)
                .eq(TradeSandboxPayoutOrder::getCallBackStatus, 1)
                .last(LIMIT_1);
        TradeSandboxPayoutOrder cashOrder = tradeSandboxPayoutOrderRepository.getOne(cashOrderQuery);

        // 如果代付、代付都已经回调成功，则 验证订单通知信息
        if (Objects.nonNull(payOrder) || Objects.nonNull(cashOrder)) {
            return SandboxMerchantStep.NOTIFICATION.name();
        }

        // 查询收款订单 排除回调状态
        payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradeSandboxPaymentOrder::getMerchantId, merchantId).last(LIMIT_1);
        payOrder = tradeSandboxPaymentOrderRepository.getOne(payOrderQuery);

        // 查询代付订单 排除回调状态
        cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradeSandboxPayoutOrder::getMerchantId, merchantId)
                .eq(TradeSandboxPayoutOrder::getCallBackStatus, 1)
                .last(LIMIT_1);
        cashOrder = tradeSandboxPayoutOrderRepository.getOne(cashOrderQuery);
        if (Objects.nonNull(payOrder) || Objects.nonNull(cashOrder)) {
            return SandboxMerchantStep.ORDER.name();
        }
        return null;
    }


    @Override
    public List<CashierPaymentMethodDTO> getSandboxPaymentMethodList4Cashier() {
        CashierDTO dto = new CashierDTO();
        initPaymentMethod(dto);
        return dto.getPaymentTypeList().stream().map(CashierPaymentTypeDTO::getPaymentMethodList)
                .flatMap(Collection::stream)
                .toList();

    }


    /**
     * 初始化支付方式
     */
    private void initPaymentMethod(CashierDTO dto) {
        List<CashierPaymentTypeDTO> paymentTypeList = new ArrayList<>();
        // va
        List<String> vaPaymentMethodList
                = Arrays.asList("BNI", "BJB", "MAYBANK", "MANDIRI", "CIMB", "DANAMON", "PERMATA", "BRI", "BCA");
        List<CashierPaymentMethodDTO> vaMethodList = vaPaymentMethodList.stream()
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
                    methodDTO.setPaymentMethod(e);
                    methodDTO.setPaymentAbbr(e.toUpperCase());
                    methodDTO.setPaymentType(PaymentTypeEnum.VIRTUAL_ACCOUNT.getCode());
                    return methodDTO;
                }).toList();
        CashierPaymentTypeDTO vaPaymentType = new CashierPaymentTypeDTO();
        vaPaymentType.setPaymentType(PaymentTypeEnum.VIRTUAL_ACCOUNT.getCode());
        vaPaymentType.setPaymentMethodList(vaMethodList);
        paymentTypeList.add(vaPaymentType);

        // Qris
        List<String> qrisPaymentMethodList = Collections.singletonList("QRIS");
        List<CashierPaymentMethodDTO> qrisMethodList = qrisPaymentMethodList.stream()
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
                    methodDTO.setPaymentMethod(e);
                    methodDTO.setPaymentAbbr(e.toUpperCase());
                    methodDTO.setPaymentType(PaymentTypeEnum.QRIS.getCode());
                    return methodDTO;
                }).toList();
        CashierPaymentTypeDTO qrisPaymentType = new CashierPaymentTypeDTO();
        qrisPaymentType.setPaymentType(PaymentTypeEnum.QRIS.getCode());
        qrisPaymentType.setPaymentMethodList(qrisMethodList);
        paymentTypeList.add(qrisPaymentType);

        // wallet
        List<String> walletPaymentMethodList
                = Arrays.asList("W_OVO", "W_DANA", "W_GOPAY", "W_SHOPEEPAY", "W_LINKAJA");
        List<CashierPaymentMethodDTO> walletMethodList = walletPaymentMethodList.stream()
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
                    methodDTO.setPaymentMethod(e);
                    methodDTO.setPaymentAbbr(e.toUpperCase());
                    methodDTO.setPaymentType(PaymentTypeEnum.E_WALLET.getCode());
                    return methodDTO;
                }).toList();
        CashierPaymentTypeDTO walletPaymentType = new CashierPaymentTypeDTO();
        walletPaymentType.setPaymentType(PaymentTypeEnum.E_WALLET.getCode());
        walletPaymentType.setPaymentMethodList(walletMethodList);
        paymentTypeList.add(walletPaymentType);

        // card cc
        List<String> cardPaymentMethodList = Arrays.asList("C_VISA", "C_MASTER_CARD", "C_JCB");
        List<CashierPaymentMethodDTO> retailStoreMethodList = cardPaymentMethodList.stream()
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
                    methodDTO.setPaymentMethod(e);
                    methodDTO.setPaymentAbbr(e.toUpperCase());
                    methodDTO.setPaymentType(PaymentTypeEnum.CREDIT_CARD.getCode());
                    return methodDTO;
                }).toList();
        CashierPaymentTypeDTO cardPaymentType = new CashierPaymentTypeDTO();
        cardPaymentType.setPaymentType(PaymentTypeEnum.CREDIT_CARD.getCode());
        cardPaymentType.setPaymentMethodList(retailStoreMethodList);
        paymentTypeList.add(cardPaymentType);

        // store
        List<String> storePaymentMethodList = Arrays.asList("S_ALFAMART", "S_INDOMARET");
       /* List<CashierPaymentMethodDTO> creditCardMethodList = storePaymentMethodList.stream()
                .map(e -> {
                    CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
                    methodDTO.setPaymentMethod(e);
                    methodDTO.setPaymentAbbr(e.toUpperCase());
                    methodDTO.setPaymentType(PaymentTypeEnum.RETAILSTORE.getCode());
                    return methodDTO;
                }).toList();
        CashierPaymentTypeDTO storePaymentType = new CashierPaymentTypeDTO();
        storePaymentType.setPaymentType(PaymentTypeEnum.RETAILSTORE.getCode());
        storePaymentType.setPaymentMethodList(creditCardMethodList);
        paymentTypeList.add(storePaymentType);*/

        // 支付方式
        dto.setPaymentTypeList(paymentTypeList);
    }


    /**
     * 查询支付方式
     */
    private CashierPaymentMethodDTO buildPaymentMethod(String paymentMethod) {
        CashierPaymentMethodDTO methodDTO = new CashierPaymentMethodDTO();
        methodDTO.setPaymentMethod(paymentMethod);

        String paymentAbbr = Optional.of(paymentMethod).map(e -> e.split("_"))
                .filter(e -> e.length > 1)
                .map(e -> e[1])
                .map(String::toUpperCase)
                .orElse(paymentMethod.toUpperCase());
        methodDTO.setPaymentAbbr(paymentAbbr);

        Integer paymentType;
        if (paymentMethod.startsWith("QRIS")) {
            paymentType = PaymentTypeEnum.QRIS.getCode();
        } else if (paymentMethod.startsWith("C_")) {
            paymentType = PaymentTypeEnum.CREDIT_CARD.getCode();
        } else if (paymentMethod.startsWith("W_")) {
            paymentType = PaymentTypeEnum.E_WALLET.getCode();
        } else {
            paymentType = PaymentTypeEnum.VIRTUAL_ACCOUNT.getCode();
        }
        methodDTO.setPaymentType(paymentType);
        return methodDTO;
    }

    /**
     * 计算过期时间间隔（秒数）
     */
    private long getExpiryPeriod(LocalDateTime tradeTime) {
        if (Objects.isNull(tradeTime)) {
            return 0;
        }
        LocalDateTime expiryTime = tradeTime.plusSeconds(TRADE_EXPIRY_PERIOD_MAX);
        Duration duration = Duration.between(LocalDateTime.now(), expiryTime);
        long durationSeconds = duration.getSeconds();
        if (durationSeconds <= 0) {
            return 0;
        }
        return durationSeconds;
    }
}
