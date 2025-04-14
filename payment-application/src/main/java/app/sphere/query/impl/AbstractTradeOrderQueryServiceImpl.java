package app.sphere.query.impl;

import app.sphere.command.dto.trade.result.MerchantResultDTO;
import app.sphere.command.dto.trade.result.PaymentResultDTO;
import app.sphere.command.dto.trade.result.TradeResultDTO;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * 交易订单查询抽象服务实现类
 * 提供交易结果解析的通用方法，包括：
 * 1. 商户信息解析
 * 2. 支付信息解析
 * 3. 错误信息解析
 */
@Slf4j
public abstract class AbstractTradeOrderQueryServiceImpl {

    /**
     * 解析商户信息
     * 从交易结果JSON字符串中提取商户相关信息
     *
     * @param tradeResult 交易结果JSON字符串
     * @return 商户信息DTO，如果解析失败则返回空对象
     */
    protected MerchantResultDTO parseMerchantResult(String tradeResult) {
        log.debug("开始解析商户信息, tradeResult={}", tradeResult);
        MerchantResultDTO result = Optional.ofNullable(tradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .orElse(new MerchantResultDTO());
        log.debug("商户信息解析完成, result={}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 解析支付信息
     * 从交易结果JSON字符串中提取支付相关信息
     *
     * @param paymentResult 交易结果JSON字符串
     * @return 支付信息DTO，如果解析失败则返回空对象
     */
    protected PaymentResultDTO parsePaymentResult(String paymentResult) {
        log.debug("开始解析支付信息, paymentResult={}", paymentResult);
        PaymentResultDTO result = Optional.ofNullable(paymentResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getPaymentResult)
                .orElse(new PaymentResultDTO());
        log.debug("支付信息解析完成, result={}", JSONUtil.toJsonStr(result));
        return result;
    }

    /**
     * 解析错误消息
     * 从交易结果中提取错误信息
     *
     * @param result 交易结果JSON字符串
     * @return 错误消息，如果解析失败则返回null
     */
    protected String getErrorMsg(String result) {
        log.debug("开始解析错误消息, result={}", result);
        String errorMsg = parsePaymentResult(result).getErrorMsg();
        log.debug("错误消息解析完成, errorMsg={}", errorMsg);
        return errorMsg;
    }

}
