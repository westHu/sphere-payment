package infrastructure.sphere.remote.channel;

import cn.hutool.core.lang.Assert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import share.sphere.exception.PaymentException;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static share.sphere.exception.ExceptionCode.PAYMENT_ERROR;

@Slf4j
@Service
public class ChannelServiceDispatcher {

    private static final Map<ChannelEnum, ChannelService> SERVICE_ENUM_MAP = new EnumMap<>(ChannelEnum.class);

    @Resource
    List<ChannelService> channelServiceList;

    /**
     * init
     */
    @PostConstruct
    public void dispatcherInit() {
        if (CollectionUtils.isEmpty(channelServiceList)) {
            return;
        }

        channelServiceList.forEach(e -> SERVICE_ENUM_MAP.put(e.getChannelName(), e));
    }

    /**
     * find channelService
     */
    public ChannelService getService(ChannelEnum channelEnum) {
        String channelEnumName = channelEnum.getName();
        log.info("getService channelEnumName={}", channelEnumName);
        ChannelService channelService = SERVICE_ENUM_MAP.get(channelEnum);
        Assert.notNull(channelService, () -> new PaymentException(PAYMENT_ERROR, "No available channel service. " + channelEnumName));
        return channelService;
    }
}