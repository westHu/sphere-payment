package infrastructure.sphere.remote;

import cn.hutool.core.lang.Assert;
import share.sphere.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static share.sphere.exception.ExceptionCode.PAYMENT_ERROR;

@Slf4j
@Service
public class ChannelServiceDispatcher {

    private static final List<ChannelEnum> MOCK_CHANNEL_LIST = Arrays.asList(ChannelEnum.C_001);
    private static final Map<ChannelEnum, ChannelService> channelServiceMap = new HashMap<>();

    @Resource
    List<ChannelService> channelServiceList;

    @Value("${spring.profiles.active}")
    String activeEnv;


    /**
     * init
     */
    @PostConstruct
    public void dispatcherInit() {
        if (CollectionUtils.isEmpty(channelServiceList)) {
            return;
        }

        channelServiceList.forEach(e -> channelServiceMap.put(e.getChannelName(), e));
    }

    /**
     * find channelService
     */
    public ChannelService getService(ChannelEnum channelEnum) {
        String channelEnumName = channelEnum.getName();
        log.info("getService channelEnumName={}", channelEnumName);
        if (StringUtils.equalsIgnoreCase(activeEnv, "test") && MOCK_CHANNEL_LIST.contains(channelEnum)) {
            channelEnum = ChannelEnum.C_MOCK;
        }
        ChannelService channelService = channelServiceMap.get(channelEnum);
        Assert.notNull(channelService,
                () -> new PaymentException(PAYMENT_ERROR, "No available channel service. " + channelEnumName));
        return channelService;
    }
}