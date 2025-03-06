package com.paysphere.manager;

import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.paysphere.cache.RedisService;
import com.paysphere.db.entity.PaymentChannel;
import com.paysphere.db.entity.PaymentChannelMethod;
import com.paysphere.exception.PaymentException;
import com.paysphere.mq.RocketMqProducer;
import com.paysphere.remote.ChannelParam;
import com.paysphere.remote.ChannelResult;
import com.paysphere.repository.PaymentChannelMethodService;
import com.paysphere.repository.PaymentChannelService;
import com.paysphere.utils.PlaceholderUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.paysphere.TradeConstant.LIMIT_1;
import static com.paysphere.TradeConstant.SOCKET_TIME_OUT;
import static com.paysphere.TradeConstant.SOCKET_UNKNOWN;
import static com.paysphere.exception.ExceptionCode.CHANNEL_ERROR;

@Slf4j
@Component
public class ChannelManager {

    @Resource
    OkHttpClient okHttpClient;
    @Resource
    PaymentChannelService paymentChannelService;
    @Resource
    PaymentChannelMethodService paymentChannelMethodMapper;
    @Resource
    RedisService redisService;
    @Resource
    RocketMqProducer rocketMqProducer;


    /**
     * 解析channelMethod配置
     */
    protected <R1, R2> Pair<R1, R2> getChannelConfig(PaymentChannel channel, Class<R1> uClass, Class<R2> lClass) {
        String channelName = channel.getChannelName();
        R1 urlDTO = JSONUtil.toBean(channel.getUrl(), uClass);
        R2 licenseDTO = JSONUtil.toBean(channel.getLicense(), lClass);
        if (Objects.nonNull(urlDTO) && Objects.nonNull(licenseDTO)) {
            return new Pair<>(urlDTO, licenseDTO);
        }

        log.error("Channel config exception: url/license, channel={}", channelName);
        throw new PaymentException("Channel config error:" + channelName);
    }

    /**
     * 构建初始request
     */
    @SneakyThrows
    protected <T> T initRequest(PaymentChannelMethod channelMethod, Class<T> tClass) {
        return Optional.ofNullable(channelMethod.getPaymentAttribute())
                .map(e -> JSONUtil.toBean(e, tClass))
                .orElse(tClass.newInstance());
    }

    /**
     * 调用 post to channel
     */
    protected <T> ChannelResult<T> postToChannel(ChannelParam param, Class<T> tClass) {
        String tradeNo = param.getTradeNo();
        Request.Builder request = this.getDefaultPostHttpRequest(tradeNo, param, tClass);
        try (Response execute = okHttpClient.newCall(request.build()).execute()) {
            //校验code
            int code = execute.code();
            log.info("tradeNo={} postToChannel return code={}", tradeNo, code);
            if (!execute.isSuccessful()) {
                String errorMsg;
                ResponseBody body = execute.body();
                if (Objects.isNull(body)) {
                    errorMsg = "response body is null";
                } else {
                    errorMsg = body.string();
                }
                log.error("tradeNo={} postToChannel httpResponse failed. errorMsg={}", tradeNo, errorMsg);
                return new ChannelResult<>(errorMsg);
            }
            return this.processHttpResponse(tradeNo, execute, tClass);
        } catch (SocketTimeoutException se) {
            log.error("tradeNo={} postToChannel SocketTimeoutException: ", tradeNo, se);
            String msg = "TradeNo: " + tradeNo +
                    "\nRequest Url: " + param.getUrl() +
                    "\nRequest Header: " + param.getHeaderMap() +
                    "\nRequest Body: " + param.getReq() +
                    "\nTimeoutException !";
            return new ChannelResult<>(SOCKET_TIME_OUT);

        } catch (Exception e) {
            log.error("tradeNo={} postToChannel exception: ", tradeNo, e);
            String msg = "TradeNo: " + tradeNo +
                    "\nRequest Url: " + param.getUrl() +
                    "\nRequest Header: " + param.getHeaderMap() +
                    "\nRequest Body: " + param.getReq() +
                    "\nException: " + e.getMessage();
            return new ChannelResult<>(SOCKET_UNKNOWN);
        }
    }

    private <T> Request.Builder getDefaultPostHttpRequest(String tradeNo, ChannelParam param, Class<T> tClass) {
        log.info("tradeNo={} postToChannel class={}, \n param={}", tradeNo, tClass.getSimpleName(), JSONUtil.toJsonStr(param));
        Request.Builder request = new Request.Builder()
                .post(RequestBody.create(MediaType.get(param.getMediaType()), param.getReq()))
                .url(param.getUrl());
        if (MapUtils.isNotEmpty(param.getHeaderMap())) {
            for (Map.Entry<String, String> entry : param.getHeaderMap().entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return request;
    }

    @SneakyThrows
    private <T> ChannelResult<T> processHttpResponse(String tradeNo, Response execute, Class<T> tClass) {
        //校验body
        ResponseBody body = execute.body();
        if (Objects.isNull(body)) {
            String errorMsg = PlaceholderUtil.resolve(CHANNEL_ERROR.getMessage(), "Body is null");
            log.error("tradeNo={} postToChannel exception: {}", tradeNo, errorMsg);
            return new ChannelResult<>(errorMsg);
        }
        String bodyStr = body.string();
        log.info("tradeNo={} postToChannel channel return  \nbody={}", tradeNo, bodyStr);

        //vitrify return body
        if (StringUtils.isBlank(bodyStr)) {
            String errorMsg = PlaceholderUtil.resolve(CHANNEL_ERROR.getMessage(), "Body string is null");
            log.error("tradeNo={} postToChannel exception: {}", tradeNo, errorMsg);
            return new ChannelResult<>(errorMsg);
        }

        //transfer dto
        T dto = JSONUtil.toBean(bodyStr, tClass);
        if (Objects.isNull(dto)) {
            String errorMsg = PlaceholderUtil.resolve(CHANNEL_ERROR.getMessage(), "Transfer fail");
            log.error("tradeNo={} postToChannel exception: {}", tradeNo, errorMsg);
            return new ChannelResult<>(errorMsg);
        }
        return new ChannelResult<>(dto);
    }

    /**
     * 查询渠道信息，缓存加持
     */
    protected PaymentChannel getChannel(String channelName) {
        String key = "LOCK_KEY_CHANNEL" + channelName;
        Object obj = redisService.get(key);
        PaymentChannel channel = Optional.ofNullable(obj).map(Object::toString)
                .map(e -> JSONUtil.toBean(e, PaymentChannel.class))
                .orElse(null);

        if (Objects.isNull(channel)) {
            QueryWrapper<PaymentChannel> channelQuery = new QueryWrapper<>();
            channelQuery.lambda().eq(PaymentChannel::getChannelName, channelName).last(LIMIT_1);
            channel = paymentChannelService.getOne(channelQuery);
            if (Objects.nonNull(channel)) {
                channel.setCreateTime(null);
                channel.setUpdateTime(null);
                redisService.set(key, JSONUtil.toJsonStr(channel), 5 * 60); //业务决定
            }
        }
        return channel;
    }

    /**
     * 查询渠道信息
     */
    protected <K, V> Pair<K, V> getChannelPair(String channelName, Class<K> kClass, Class<V> vClass) {
        PaymentChannel channel = getChannel(channelName);
        return getChannelConfig(channel, kClass, vClass);
    }

}
