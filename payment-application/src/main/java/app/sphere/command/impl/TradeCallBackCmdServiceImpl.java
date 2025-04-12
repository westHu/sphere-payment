package app.sphere.command.impl;

import app.sphere.command.TradeCallBackCmdService;
import app.sphere.command.cmd.TradeCallbackCommand;
import app.sphere.command.dto.TradeCallBackResultAttributeDTO;
import app.sphere.command.dto.TradeCallBackResultDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackBodyDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackDTO;
import app.sphere.command.dto.trade.callback.TradeCallBackMoneyDTO;
import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import app.sphere.manager.CallbackManager;
import cn.hutool.core.lang.Assert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import domain.sphere.repository.TradePaymentCallBackResultRepository;
import domain.sphere.repository.TradePaymentOrderRepository;
import domain.sphere.repository.TradePayoutCallBackResultRepository;
import domain.sphere.repository.TradePayoutOrderRepository;
import infrastructure.sphere.db.entity.TradePaymentCallBackResult;
import infrastructure.sphere.db.entity.TradePaymentOrder;
import infrastructure.sphere.db.entity.TradePayoutCallBackResult;
import infrastructure.sphere.db.entity.TradePayoutOrder;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import share.sphere.TradeConstant;
import share.sphere.enums.CallBackStatusEnum;
import share.sphere.enums.PaymentStatusEnum;
import share.sphere.enums.TradeModeEnum;
import share.sphere.enums.TradePayoutSourceEnum;
import share.sphere.enums.TradeTypeEnum;
import share.sphere.exception.ExceptionCode;
import share.sphere.exception.PaymentException;
import share.sphere.utils.ValidationUtil;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


@Slf4j
@Service
public class TradeCallBackCmdServiceImpl implements TradeCallBackCmdService {

    @Resource
    TradePaymentOrderRepository tradePaymentOrderRepository;
    @Resource
    TradePayoutOrderRepository tradePayoutOrderRepository;
    @Resource
    TradePaymentCallBackResultRepository tradePaymentCallBackResultRepository;
    @Resource
    TradePayoutCallBackResultRepository tradePayoutCallBackResultRepository;
    @Resource
    CallbackManager callbackManager;

    /**
     * 消费消息进行订单回调
     */
    @Override
    public void handlerTradeCallback(TradeCallBackDTO callBackDTO) {
        String tradeNo = Optional.of(callBackDTO).map(TradeCallBackDTO::getBody)
                .map(TradeCallBackBodyDTO::getTradeNo)
                .orElseThrow(() -> new PaymentException(ExceptionCode.SYSTEM_ERROR, "callback tradeNo validated"));
        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(tradeNo);
        log.info("messageHandlerTradeCallback tradeTypeEnum={}, tradeNo={}", tradeTypeEnum, tradeNo);

        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            doPayOrderCallBack(callBackDTO);
            return;

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
            doCashOrderCallBack(callBackDTO);
            return;
        }
        throw new PaymentException(ExceptionCode.SYSTEM_ERROR);
    }

    /**
     * 进行订单号来指定回调
     */
    @Override
    public boolean handlerTradeCallback(TradeCallbackCommand command) {
        log.info("apiHandlerTradeCallback command={}", JSONUtil.toJsonStr(command));

        TradeTypeEnum tradeTypeEnum = TradeTypeEnum.tradeNoToTradeType(command.getTradeNo());
        log.info("apiHandlerTradeCallback tradeTypeEnum={}", tradeTypeEnum);

        // 按订单类型进行回调
        if (tradeTypeEnum.equals(TradeTypeEnum.PAYMENT)) {
            return doPayOrderCallBack(command);

        } else if (tradeTypeEnum.equals(TradeTypeEnum.PAYOUT)) {
            return doCashOrderCallBack(command);
        }
        throw new PaymentException(ExceptionCode.TRADE_CALLBACK_ERROR);
    }

    /**
     * 收款订单回调
     */
    private void doPayOrderCallBack(TradeCallBackDTO dto) {
        String callBackResult = null;
        try {
            // 参数校验, 参数不通过则不回调
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doPayOrderCallBack message validate false. errorMsg={}", errorMsg);
                callBackResult = errorMsg;
                return;
            }

            // 执行回调
            callBackResult = callbackManager.apiCallback(dto);
        } catch (Exception e) {
            log.error("doPayOrderCallBack exception:", e);
            callBackResult = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
        } finally {
            String tradeNo = dto.getBody().getTradeNo();

            // 新增回调记录
            callBackResult = getCallbackResult(callBackResult);
            TradePaymentCallBackResult result = buildTradePayCallBackResult(dto, callBackResult, "system");
            boolean save = tradePaymentCallBackResultRepository.save(result);
            log.info("sphere doPayOrderCallBack callback add record result={}", save);

            // 更新回调状态、次数
            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePaymentOrder::getTradeNo, tradeNo);
            boolean update = tradePaymentOrderRepository.update(payOrderUpdate);
            log.info("doPayOrderCallBack callback update order result={}", update);
        }
    }

    /**
     * 代付订单回调
     */
    private void doCashOrderCallBack(TradeCallBackDTO dto) {
        String callBackResult = null;
        try {
            // 参数校验
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doCashOrderCallBack message validate false. errorMsg={}", errorMsg);
                callBackResult = errorMsg;
                return;
            }

            String source = dto.getSource();
            String url = dto.getUrl();
            log.info("doCashOrderCallBack source={}, url={}", source, url);

            // 执行回调
          if (source.equals(TradePayoutSourceEnum.API.name())) {
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
            tradePayoutCallBackResultRepository.save(result);

            // 更新回调状态、次数
            UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradePayoutOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePayoutOrder::getTradeNo, tradeNo);
            tradePayoutOrderRepository.update(cashOrderUpdate);
        }

    }

    /**
     * 收款订单回调
     */
    private boolean doPayOrderCallBack(TradeCallbackCommand command) {
        String tradeNo = command.getTradeNo();

        // 校验订单是否存在
        QueryWrapper<TradePaymentOrder> payOrderQuery = new QueryWrapper<>();
        payOrderQuery.lambda().eq(TradePaymentOrder::getTradeNo, tradeNo).last(TradeConstant.LIMIT_1);
        TradePaymentOrder order = tradePaymentOrderRepository.getOne(payOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        // 判断订单状态 状态非最终状态不需要回调
        Integer paymentStatus = order.getPaymentStatus();
        if (Objects.isNull(paymentStatus) || !PaymentStatusEnum.getFinalStatus().contains(paymentStatus)) {
            log.error("doPayOrderCallBack status not final status. paymentStatus={}", paymentStatus);
            throw new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FINAL, tradeNo);
        }

        String callBackResult = null;
        TradeCallBackDTO dto = buildPayOrderCallBackDTO(order);
        try {
            // 构建&校验参数
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doPayOrderCallBack validation tradeNo={} errorMsg={}", tradeNo, errorMsg);
                throw new PaymentException(ExceptionCode.TRADE_CALLBACK_ERROR, errorMsg);
            }

            // 执行回调
            callBackResult = callbackManager.apiCallback(dto);
            log.info("doPayOrderCallBack callBackResult={}", callBackResult);

        } catch (Exception e) {
            log.error("doPayOrderCallBack exception:", e);
            callBackResult = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;

        } finally {
            // 新增回调记录
            callBackResult = getCallbackResult(callBackResult);
            TradePaymentCallBackResult result = buildTradePayCallBackResult(dto, callBackResult, command.getOperator());
            boolean save = tradePaymentCallBackResultRepository.save(result);
            log.info("sphere doPayOrderCallBack callback add record result={}", save);

            // 更新回调状态、次数
            UpdateWrapper<TradePaymentOrder> payOrderUpdate = new UpdateWrapper<>();
            payOrderUpdate.lambda()
                    .set(TradePaymentOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePaymentOrder::getTradeNo, tradeNo);
            tradePaymentOrderRepository.update(payOrderUpdate);
        }
        return TradeConstant.CALLBACK_POST_SUCCESS.equals(callBackResult);
    }

    /**
     * 代付订单回调
     */
    private boolean doCashOrderCallBack(TradeCallbackCommand command) {
        String tradeNo = command.getTradeNo();

        QueryWrapper<TradePayoutOrder> cashOrderQuery = new QueryWrapper<>();
        cashOrderQuery.lambda().eq(TradePayoutOrder::getTradeNo, tradeNo);
        TradePayoutOrder order = tradePayoutOrderRepository.getOne(cashOrderQuery);
        Assert.notNull(order, () -> new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FOUND, tradeNo));

        // 判断订单状态， 非最终状态不需要回调
        Integer paymentStatus = order.getPaymentStatus();
        if (Objects.isNull(paymentStatus) || !PaymentStatusEnum.getFinalStatus().contains(paymentStatus)) {
            log.error("doCashOrderCallBack status not final status. paymentStatus={}", paymentStatus);
            throw new PaymentException(ExceptionCode.TRADE_ORDER_NOT_FINAL, tradeNo);
        }

        String callBackResult = null;
        TradeCallBackDTO dto = buildCashOrderCallBackDTO(order);
        try {
            // 构建&校验参数
            String errorMsg = ValidationUtil.getErrorMsg(dto);
            if (StringUtils.isNotBlank(errorMsg)) {
                log.error("doCashOrderCallBack validation tradeNo={} errorMsg={}", tradeNo, errorMsg);
                throw new PaymentException(ExceptionCode.TRADE_CALLBACK_ERROR, errorMsg);
            }

            // 执行回调
            callBackResult = callbackManager.apiCallback(dto);
            log.info("doCashOrderCallBack tradeNo={} callBackResult={}", tradeNo, callBackResult);

        } catch (Exception e) {
            log.error("doCashOrderCallBack tradeNo={} exception:", tradeNo, e);
            callBackResult = StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : TradeConstant.ERROR_TO_CHECK;
        } finally {
            // 新增回调记录
            callBackResult = getCallbackResult(callBackResult);
            TradePayoutCallBackResult result = buildTradeCashCallBackResult(dto, callBackResult, command.getOperator());
            tradePayoutCallBackResultRepository.save(result);

            // 更新回调状态、次数
            UpdateWrapper<TradePayoutOrder> cashOrderUpdate = new UpdateWrapper<>();
            cashOrderUpdate.lambda()
                    .set(TradePayoutOrder::getCallBackStatus, result.getCallBackStatus())
                    .setSql(TradeConstant.CALLBACK_SQL)
                    .setSql(TradeConstant.VERSION_SQL)
                    .eq(TradePayoutOrder::getTradeNo, tradeNo);
            tradePayoutOrderRepository.update(cashOrderUpdate);
        }
        return TradeConstant.CALLBACK_POST_SUCCESS.equals(callBackResult);
    }

    /**
     * 构建收款回调返回体
     */
    private TradeCallBackDTO buildPayOrderCallBackDTO(TradePaymentOrder payOrder) {
        String finishPaymentUrl = Optional.of(payOrder).map(TradePaymentOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getFinishPaymentUrl).orElse(null);

        // 如果订单信息中么有
        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(payOrder.getTradeNo());
        bodyDTO.setOrderNo(payOrder.getOrderNo());
        bodyDTO.setMerchantId(payOrder.getMerchantId());
        bodyDTO.setMerchantName(payOrder.getMerchantName());
        bodyDTO.setStatus(PaymentStatusEnum.codeToEnum(payOrder.getPaymentStatus()).getName());
        bodyDTO.setTransactionTime(LocalDateTime.now().toString());

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
        callBackDTO.setSource(null);
        callBackDTO.setUrl(finishPaymentUrl);
        callBackDTO.setBody(bodyDTO);
        return callBackDTO;
    }


    /**
     * 构建代付回调返回体
     */
    public TradeCallBackDTO buildCashOrderCallBackDTO(TradePayoutOrder cashOrder) {
        String finishCashUrl = Optional.of(cashOrder).map(TradePayoutOrder::getTradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .map(MerchantResultDTO::getFinishCashUrl)
                .orElse(null);

        // 如果订单信息中么有,则从配置中再次获取
        TradeCallBackBodyDTO bodyDTO = new TradeCallBackBodyDTO();
        bodyDTO.setTradeNo(cashOrder.getTradeNo());
        bodyDTO.setOrderNo(cashOrder.getOrderNo());
        bodyDTO.setMerchantId(cashOrder.getMerchantId());
        bodyDTO.setMerchantName(cashOrder.getMerchantName());
        bodyDTO.setStatus(PaymentStatusEnum.codeToEnum(cashOrder.getPaymentStatus()).getName());
        bodyDTO.setTransactionTime(LocalDateTime.now().toString());

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
        callBackDTO.setSource(null);
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
        result.setCallBackTime(System.currentTimeMillis());
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
        log.info("sphere buildTradeCashCallBackResult statusEnum={}", statusEnum.name());

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
        result.setCallBackTime(System.currentTimeMillis());
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
