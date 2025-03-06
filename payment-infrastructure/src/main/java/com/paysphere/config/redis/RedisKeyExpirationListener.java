package com.paysphere.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Slf4j
// @Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * 使用该方法监听 当我们都key失效的时候执行该方法
     */
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expireKey = message.toString();
        log.info("我已经监测到键值失效啦键值是" + expireKey + "你可以执行你的业务了");
    }

}