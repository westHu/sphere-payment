package com.paysphere.command.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.paysphere.TradeConstant;
import com.paysphere.command.TradeCallBackCmdService;
import com.paysphere.command.cmd.TradeCallbackCmd;
import com.paysphere.command.dto.TradeCallBackResultAttributeDTO;
import com.paysphere.command.dto.TradeCallBackResultDTO;
import com.paysphere.command.dto.TradePaymentAttributeDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackDTO;
import com.paysphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import com.paysphere.command.dto.trade.result.MerchantResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import com.paysphere.db.entity.TradePaymentCallBackResult;
import com.paysphere.db.entity.TradePaymentOrder;
import com.paysphere.db.entity.TradePayoutCallBackResult;
import com.paysphere.db.entity.TradePayoutOrder;
import com.paysphere.enums.CallBackStatusEnum;
import com.paysphere.enums.PaymentStatusEnum;
import com.paysphere.enums.TradeCashSourceEnum;
import com.paysphere.enums.TradeModeEnum;
import com.paysphere.enums.TradePaymentSourceEnum;
import com.paysphere.enums.TradeTypeEnum;
import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import com.paysphere.manager.CallbackManager;
import com.paysphere.repository.TradePaymentCallBackResultService;
import com.paysphere.repository.TradePaymentOrderService;
import com.paysphere.repository.TradePayoutCallBackResultService;
import com.paysphere.repository.TradePayoutOrderService;
import com.paysphere.utils.ValidationUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class TradeCallBackCmdServiceImpl implements TradeCallBackCmdService {

    @Resource
    TradePaymentOrderService tradePaymentOrderService;
    @Resource
    TradePayoutOrderService tradePayoutOrderService;
    @Resource
    TradePaymentCallBackResultService tradePaymentCallBackResultService;
    @Resource
    TradePayoutCallBackResultService tradePayoutCallBackResultService;
    @Resource
    CallbackManager callbackManager;


    /**
     * 消费消息进行订单回调
     */
    @Override
    public boolean handlerTradeCallback(TradeCallBackDTO callBackDTO) {
        String tradeNo = Optional.of(callBackDTO).map(TradeCallBackDTO::getBody)
                .map(TradeCallBackBodyDTO::getTradeNo)
                .orElseThrow(() -> new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "callback tradeNo validated"));
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(tradeNo);
        log.info("messageHandlerTradeCallback tradeTypeEnum={}, tradeNo={}", tradeTypeEnum, tradeNo);

        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            return doPayOrderCallBack(callBackDTO);

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
            return doCashOrderCallBack(callBackDTO);
        }
        throw new PaymentException(ExceptionCode.UNSUPPORTED_ORDER_TYPE);
    }

    /**
     * 进行订单号来指定回调
     */
    @Override
    public boolean handlerTradeCallback(TradeCallbackCmd command) {
        log.info("apiHandlerTradeCallback command={}", JSONUtil.toJsonStr(command));

        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(command.getTradeNo());
        log.info("apiHandlerTradeCallback tradeTypeEnum={}", tradeTypeEnum);

        // 按订单类型进行回调
        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            return doPayOrderCallBack(command);

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
            return doCashOrderCallBack(command);
        }
        throw new PaymentException(ExceptionCode.UNSUPPORTED_ORDER_TYPE);
    }

    /**
     * 收款订单回调
     */
    private boolean doPayOrderCallBack(TradeCallBackDTO dto) {
        String callBackResult = null;
        try {
            // 参数校验, 参数不通过则不回调
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doPayOrderCallBack message validate false. errorMsg={}", errorMsg);
                callBackResult = errorMsg;
                return true;
            }

            TradeCallBackBodyDTO body = dto.getBody();
            String source = dto.getSource();
            String url = dto.getUrl();
            log.info("doPayOrderCallBack source={}, url={}", source, url);

            // 执行回调
            if (source.equals(TradePaymentSourceEnum.API.name())) {
                callBackResult = callbackManager.apiCallback(dto);
            } else {
                callBackResult = "unsupported tradePaySource: " + source;
            }

        } catch (Exception e) {
            log.error("doPayOrderCallBack exception:", e);
            callBackResult = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
        } finally {
            String tradeNo = dto.getBody().getTradeNo();

            // 新增回调记录
            callBackResult = getCallbackResult(callBackResult);
            TradePaymentCallBackResult result = buildTradePayCallBackResult(dto, callBackResult, "system");
            boolean save = tradePaymentCallBackResultService.save(result);
            log.info("paysphere doPayOrderCallBack callback add record result={}", save);

            // 更新回调状态、次数
            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePaymentOrder::getTradeNo, tradeNo);
            boolean update = tradePaymentOrderService.update(payOrderUpdate);
            log.info("doPayOrderCallBack callback update order result={}", update);
        }
        return TradeConstant.CALLBACK_POST_SUCCESS.equals(callBackResult);
    }


    /**
     * 代付订单回调
     */
    private boolean doCashOrderCallBack(TradeCallBackDTO dto) {
        String callBackResult = null;
        try {
            // 参数校验
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doCashOrderCallBack message validate false. errorMsg={}", errorMsg);
                callBackResult = errorMsg;
                return true;
            }

            String source = dto.getSource();
            String url = dto.getUrl();
            log.info("doCashOrderCallBack source={}, url={}", source, url);

            // 执行回调
          if (source.equals(TradeCashSourceEnum.API.name())) {
                callBackResult = callbackManager.apiCallback(dto);
            } else {
                callBackResult = "unsupported tradeCashSource: " + source;
            }

        } catch (Exception e) {
            log.error("doCashOrderCallBack exception:", e);
            callBackResult = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
        } finally {
            String tradeNo = dto.getBody().getTradeNo();

            // 新增回调记录
            callBackResult = getCallbackResult(callBackResult);
            TradePayoutCallBackResult result = buildTradeCashCallBackResult(dto, callBackResult, "system");
            tradePayoutCallBackResultService.save(result);

            // 更新回调状态、次数
            UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradePayoutOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePayoutOrder::getTradeNo, tradeNo);
            tradePayoutOrderService.update(cashOrderUpdate);
        }

        return TradeConstant.CALLBACK_POST_SUCCESS.equals(callBackResult);
    }

    /**
     * 收款订单回调
     */
    private boolean doPayOrderCallBack(TradeCallbackCmd command) {
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderService.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.PAY_ORDER_NOT_EXIST, tradeNo));

        // 判断订单状态 状态非最终状态不需要回调
        Integer paymentStatus = order.getPaymentStatus();
        if (Objects.isNull(paymentStatus) || !PaymentStatusEnum.getFinalStatus().contains(paymentStatus)) {
            log.error("doPayOrderCallBack status not final status. paymentStatus={}", paymentStatus);
            throw new PaymentException(ExceptionCode.PAY_ORDER_NOT_FINAL_STATUS, tradeNo);
        }

        // API、Woo、qr_code 订单进行回调
        TradePaymentSourceEnum sourceEnum = TradePaymentSourceEnum.codeToEnum(order.getSource());
        log.info("doPayOrderCallBack sourceEnum={}", sourceEnum.name());
        if (!TradePaymentSourceEnum.callbackEnumList().contains(sourceEnum)) {
            log.warn("doPayOrderCallBack Order type not [API、Woo、QRCode]. can not callback, {}", order.getTradeNo());
            throw new PaymentException(ExceptionCode.CALLBACK_NOT_ALLOW_TYPE, tradeNo);
        }

        String callBackResult = null;
        TradeCallBackDTO dto = buildPayOrderCallBackDTO(order, sourceEnum);
        try {
            // 构建&校验参数
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doPayOrderCallBack validation tradeNo={} errorMsg={}", tradeNo, errorMsg);
                throw new PaymentException(ExceptionCode.CALLBACK_PARAMETER_ERROR, errorMsg);
            }

            TradeCallBackBodyDTO body = dto.getBody();
            String source = dto.getSource();
            String url = dto.getUrl();

            // 执行回调
            if (source.equals(TradePaymentSourceEnum.API.name())) {
                callBackResult = callbackManager.apiCallback(dto);
            } else {
                callBackResult = "unsupported tradePaySource: " + source;
            }
            log.info("doPayOrderCallBack callBackResult={}", callBackResult);

        } catch (Exception e) {
            log.error("doPayOrderCallBack exception:", e);
            callBackResult = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;

        } finally {
            // 新增回调记录
            callBackResult = getCallbackResult(callBackResult);
            TradePaymentCallBackResult result = buildTradePayCallBackResult(dto, callBackResult, command.getOperator());
            boolean save = tradePaymentCallBackResultService.save(result);
            log.info("paysphere doPayOrderCallBack callback add record result={}", save);

            // 更新回调状态、次数
            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePaymentOrder::getTradeNo, tradeNo);
            tradePaymentOrderService.update(payOrderUpdate);
        }
        return TradeConstant.CALLBACK_POST_SUCCESS.equals(callBackResult);
    }

    /**
     * 代付订单回调
     */
    private boolean doCashOrderCallBack(TradeCallbackCmd command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo);
        TradePayoutOrder order = tradePayoutOrderService.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.CASH_ORDER_NOT_EXIST, tradeNo));

        // 判断订单状态， 非最终状态不需要回调
        Integer paymentStatus = order.getPaymentStatus();
        if (Objects.isNull(paymentStatus) || !PaymentStatusEnum.getFinalStatus().contains(paymentStatus)) {
            log.error("doCashOrderCallBack status not final status. paymentStatus={}", paymentStatus);
            throw new PaymentException(ExceptionCode.CASH_ORDER_NOT_FINAL_STATUS, tradeNo);
        }

        // 非API订单无法进行回调
        TradeCashSourceEnum sourceEnum = TradeCashSourceEnum.codeToEnum(order.getSource());
        log.info("doCashOrderCallBack sourceEnum={}", sourceEnum.name());
        if (!sourceEnum.equals(TradeCashSourceEnum.API)) {
            log.warn("doCashOrderCallBack Order type not api. can not to callback, {}", order.getTradeNo());
            throw new PaymentException(ExceptionCode.CALLBACK_NOT_ALLOW_TYPE, tradeNo);
        }

        String callBackResult = null;
        TradeCallBackDTO dto = buildCashOrderCallBackDTO(order, sourceEnum);
        try {
            // 构建&校验参数
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doCashOrderCallBack validation tradeNo={} errorMsg={}", tradeNo, errorMsg);
                throw new PaymentException(ExceptionCode.CALLBACK_PARAMETER_ERROR, errorMsg);
            }

            String source = dto.getSource();

            // 执行回调
          if (source.equals(TradeCashSourceEnum.API.name())) {
                callBackResult = callbackManager.apiCallback(dto);
            } else {
                callBackResult = "unsupported tradeCashSource: " + source;
            }
            log.info("doCashOrderCallBack tradeNo={} callBackResult={}", tradeNo, callBackResult);

        } catch (Exception e) {
            log.error("doCashOrderCallBack tradeNo={} exception:", tradeNo, e);
            callBackResult = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
        } finally {
            // 新增回调记录
            callBackResult = getCallbackResult(callBackResult);
            TradePayoutCallBackResult result = buildTradeCashCallBackResult(dto, callBackResult, command.getOperator());
            tradePayoutCallBackResultService.save(result);

            // 更新回调状态、次数
            UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradePayoutOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePayoutOrder::getTradeNo, tradeNo);
            tradePayoutOrderService.update(cashOrderUpdate);
        }
        return TradeConstant.CALLBACK_POST_SUCCESS.equals(callBackResult);
    }

    /**
     * 构建收款回调返回体
     */
    private TradeCallBackDTO buildPayOrderCallBackDTO(TradePaymentOrder payOrder, TradePaymentSourceEnum sourceEnum) {
        String finishPaymentUrl = Optional.of(payOrder).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getFinishPaymentUrl).orElse(null);

        String shopName = Optional.of(payOrder).map(TradePaymentOrder::getAttribute)
                .map(e -> JSONUtil.toBean(e, TradePaymentAttributeDTO.class))
                .map(TradePaymentAttributeDTO::getShopName)
                .orElse(null);

        // 如果订单信息中么有,则从配置中再次获取

//        ZonedDateTime tradeTime = Optional.of(payOrder)
//                .map(TradePaymentOrder::getTradeTime)
//                .map(e -> e.atZone(TradeConstant.ZONE_ID))
//                .orElse(ZonedDateTime.now());

        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(payOrder.getTradeNo());
        bodyDTO.setOrderNo(payOrder.getOrderNo());
        bodyDTO.setMerchantId(payOrder.getMerchantId());
        bodyDTO.setMerchantName(payOrder.getMerchantName());
        bodyDTO.setShopName(shopName);
//        bodyDTO.setStatus(PaymentStatusEnum.codeToEnum(payOrder.getPaymentStatus()).getMerchantStatus());
//        bodyDTO.setTransactionTime(tradeTime.format(TradeConstant.DF_3));

        TradeCallBackMoneyDTO money = new TradeCallBackMoneyDTO();
        money.setCurrency(payOrder.getCurrency());
        money.setAmount(payOrder.getAmount());
        bodyDTO.setMoney(money);

        TradeCallBackMoneyDTO fee = new TradeCallBackMoneyDTO();
        fee.setCurrency(payOrder.getCurrency());
        fee.setAmount(payOrder.getMerchantFee());
        bodyDTO.setFee(fee);

        TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
        callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
        callBackDTO.setSource(sourceEnum.name());
        callBackDTO.setUrl(finishPaymentUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }


    /**
     * 构建代付回调返回体
     */
    public TradeCallBackDTO buildCashOrderCallBackDTO(TradePayoutOrder cashOrder, TradeCashSourceEnum sourceEnum) {
        String finishCashUrl = Optional.of(cashOrder).map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getFinishCashUrl)
                .orElse(null);

        // 如果订单信息中么有,则从配置中再次获取


//        ZonedDateTime tradeTime = Optional.of(cashOrder)
//                .map(TradePayoutOrder::getTradeTime)
//                .map(e -> e.atZone(TradeConstant.ZONE_ID))
//                .orElse(ZonedDateTime.now());

        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(cashOrder.getTradeNo());
        bodyDTO.setOrderNo(cashOrder.getOuterNo());
        bodyDTO.setMerchantId(cashOrder.getMerchantId());
        bodyDTO.setMerchantName(cashOrder.getMerchantName());
//        bodyDTO.setStatus(PaymentStatusEnum.codeToEnum(cashOrder.getPaymentStatus()).getMerchantStatus());
//        bodyDTO.setTransactionTime(tradeTime.format(TradeConstant.DF_3));

        TradeCallBackMoneyDTO money = new TradeCallBackMoneyDTO();
        money.setCurrency(cashOrder.getCurrency());
        money.setAmount(cashOrder.getAmount());
        bodyDTO.setMoney(money);

        TradeCallBackMoneyDTO fee = new TradeCallBackMoneyDTO();
        fee.setCurrency(cashOrder.getCurrency());
        fee.setAmount(cashOrder.getMerchantFee());
        bodyDTO.setFee(fee);

        TradeCallBackDTO callBackDTO = new TradeCallBackDTO();
        callBackDTO.setMode(TradeModeEnum.PRODUCTION.getMode());
        callBackDTO.setSource(sourceEnum.name());
        callBackDTO.setUrl(finishCashUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }

    /**
     * 构建收款回调体
     */
    private TradePaymentCallBackResult buildTradePayCallBackResult(TradeCallBackDTO dto,
                                                                   String callBackResult,
                                                                   String operator) {
        String url = dto.getUrl();
        TradeCallBackBodyDTO body = dto.getBody();
        String tradeNo = body.getTradeNo();
        CallBackStatusEnum statusEnum = TradeConstant.CALLBACK_POST_SUCCESS.equalsIgnoreCase(callBackResult) ?
                CallBackStatusEnum.CALLBACK_SUCCESS : CallBackStatusEnum.CALLBACK_FAILED;
        log.info("buildTradePayCallBackResult statusEnum={}", statusEnum.name());

        // 返回结果
        TradeCallBackResultDTO tradeCallBackResultDTO = new TradeCallBackResultDTO();
        tradeCallBackResultDTO.setMessage(callBackResult);
        // 扩展信息
        TradeCallBackResultAttributeDTO attributeDTO = new TradeCallBackResultAttributeDTO();
        attributeDTO.setUrl(url);
        attributeDTO.setParam(JSONUtil.toJsonStr(body));
        attributeDTO.setOperator(operator);

        TradePaymentCallBackResult result = new TradePaymentCallBackResult();
        result.setTradeNo(tradeNo);
//        result.setCallBackTime(LocalDateTime.now());
        result.setCallBackStatus(statusEnum.getCode());
        result.setCallBackResult(JSONUtil.toJsonStr(tradeCallBackResultDTO));
        result.setCreateTime(LocalDateTime.now());
        result.setAttribute(JSONUtil.toJsonStr(dto));
        return result;
    }


    /**
     * 构建代付回调体
     */
    private TradePayoutCallBackResult buildTradeCashCallBackResult(TradeCallBackDTO dto,
                                                                   String callBackResult,
                                                                   String operator) {
        String url = dto.getUrl();
        TradeCallBackBodyDTO body = dto.getBody();
        String tradeNo = body.getTradeNo();
        CallBackStatusEnum statusEnum = TradeConstant.CALLBACK_POST_SUCCESS.equalsIgnoreCase(callBackResult) ?
                CallBackStatusEnum.CALLBACK_SUCCESS : CallBackStatusEnum.CALLBACK_FAILED;
        log.info("paysphere buildTradeCashCallBackResult statusEnum={}", statusEnum.name());

        // 返回结果
        TradeCallBackResultDTO tradeCallBackResultDTO = new TradeCallBackResultDTO();
        tradeCallBackResultDTO.setMessage(callBackResult);
        // 扩展信息
        TradeCallBackResultAttributeDTO attributeDTO = new TradeCallBackResultAttributeDTO();
        attributeDTO.setUrl(url);
        attributeDTO.setParam(JSONUtil.toJsonStr(body));
        attributeDTO.setOperator(operator);

        TradePayoutCallBackResult result = new TradePayoutCallBackResult();
        result.setTradeNo(tradeNo);
//        result.setCallBackTime(LocalDateTime.now());
        result.setCallBackStatus(statusEnum.getCode());
        result.setCallBackResult(JSONUtil.toJsonStr(tradeCallBackResultDTO));
        result.setAttribute(JSONUtil.toJsonStr(attributeDTO));
        result.setCreateTime(LocalDateTime.now());
        return result;
    }


    /**
     * 商户返回过长
     */
    private String getCallbackResult(String callBackResult) {
        if (StringUtils.isNotBlank(callBackResult) && callBackResult.length() > 32) {
            callBackResult = callBackResult.substring(0, 32);
        }
        return callBackResult;
    }

}
