package com.paysphere.trade;

import com.paysphere.PaymentApplication;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentApplication.class)
public class RedisTest {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    @Test
    public void redisTest() {
        log.info("redisTest");
        redisTemplate.opsForValue().set("key01", LocalDateTime.now().toString());
        Object key01 = redisTemplate.opsForValue().get("key01");
        log.info("key01 value={}", key01);

    }
}
