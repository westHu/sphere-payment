package com.paysphere.event;

import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 领域事件发布实现类
 */
@Component
@Slf4j
public class DomainEventPublisherImpl implements DomainEventPublisher {

    @Resource
    ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publishEvent(BaseDomainEvent event) {
        log.debug("发布事件,event:{}", JSONUtil.toJsonStr(event));
        applicationEventPublisher.publishEvent(event);
    }
}