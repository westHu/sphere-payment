package com.paysphere.query.impl;

import cn.hutool.json.JSONUtil;
import com.paysphere.command.dto.TradeChannelErrorDTO;
import com.paysphere.command.dto.trade.result.ChannelResultDTO;
import com.paysphere.command.dto.trade.result.MerchantResultDTO;
import com.paysphere.command.dto.trade.result.TradeResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Optional;

import static com.paysphere.TradeConstant.ERROR_SPLIT;

@Slf4j
public abstract class AbstractTradeOrderQueryServiceImpl {

    /**
     * 解析商户信息
     */
    protected MerchantResultDTO parseMerchantResult(String tradeResult) {
        return Optional.ofNullable(tradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getMerchantResult)
                .orElse(new MerchantResultDTO());
    }

    /**
     * 解析渠道Va等信息
     */
    protected ChannelResultDTO parseChannelResult(String tradeResult) {
        return Optional.ofNullable(tradeResult)
                .map(e -> JSONUtil.toBean(e, TradeResultDTO.class))
                .map(TradeResultDTO::getChannelResult)
                .orElse(new ChannelResultDTO());
    }

    /**
     * 解析错误消息
     */
    protected String getPayErrorMsg(String result) {
        return Optional.ofNullable(result)
                .map(e -> e.split(ERROR_SPLIT))
                .filter(e -> e.length > 1)
                .map(e -> e[1])
                .map(e -> JSONUtil.toList(e, TradeChannelErrorDTO.class))
                .filter(CollectionUtils::isNotEmpty)
                .map(e -> e.get(0))
                .map(TradeChannelErrorDTO::getErrorMsg)
                .orElse(result);
    }

    /**
     * 交易错误，解析msg
     */
    protected String getCashErrorMsg(String result) {
        return Optional.of(result)
                .map(e -> e.split(ERROR_SPLIT))
                .filter(e -> e.length > 1)
                .map(e -> e[1])
                .map(e -> JSONUtil.toBean(e, TradeChannelErrorDTO.class))
                .map(TradeChannelErrorDTO::getErrorMsg)
                .orElse(result);
    }


    /**
     * 交易错误，解析channelName
     */
    protected String getCashChannelNameByMsg(String result) {
        return Optional.of(result)
                .map(e -> e.split(ERROR_SPLIT))
                .filter(e -> e.length > 1)
                .map(e -> e[1])
                .map(e -> JSONUtil.toBean(e, TradeChannelErrorDTO.class))
                .map(TradeChannelErrorDTO::getChannelName)
                .orElse("");
    }
}
