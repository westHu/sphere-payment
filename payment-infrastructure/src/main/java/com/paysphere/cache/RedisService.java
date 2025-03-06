package com.paysphere.cache;

import com.paysphere.exception.ExceptionCode;
import com.paysphere.exception.PaymentException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.AlternativeJdkIdGenerator;

import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisService {

    private final AlternativeJdkIdGenerator generator = new AlternativeJdkIdGenerator();

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    /**
     * lock with return
     */
    public <T> T lock(String key, Operator<T> operator) {
        String val = generator.generateId().toString();
        String fullKey = getRedisFullKey(key);
        boolean locked = baseLock(fullKey, val, 15, TimeUnit.SECONDS);
        log.info("Distributed lock key={} val={} result={}", key, val, locked);
        if (locked) {
            try {
                return operator.execute();
            } finally {
                unLock(key, val);
            }
        }
        throw new PaymentException(ExceptionCode.INTERNAL_SERVER_ERROR, "Key " + key + " has been processing");
    }


    /**
     * base lock
     */
    private boolean baseLock(String key, String val, long expireTime, TimeUnit unit) {
        Boolean opt = redisTemplate.opsForValue().setIfAbsent(key, val, expireTime, unit);
        return Objects.nonNull(opt) && Boolean.TRUE.equals(opt);
    }


    /**
     * unLock
     */
    public void unLock(String key, String val) {
        String fullKey = getRedisFullKey(key);
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return " +
                "0 end";
        RedisScript<Long> redisScript = new DefaultRedisScript<>(script, Long.class);
        Long execute = redisTemplate.execute(redisScript, Collections.singletonList(fullKey), val);
        log.info("Distributed unlock key={}, val={}, execute={}", key, val, execute);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(getRedisFullKey(key));
    }

    // ==================================String====================================

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true 成功 false 失败
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(getRedisFullKey(key), value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time > 0 若 time <= 0 将设置无限期
     * @return true 成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            String fullKey = getRedisFullKey(key);
            if (time > 0) {
                redisTemplate.opsForValue().set(fullKey, value, time, TimeUnit.SECONDS);
            } else {
                set(fullKey, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * setIfAbsent缓存放入
     */
    public boolean setIfAbsent(String key, Object value, long time) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, value, time, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("redis setIfAbsent time", e);
            return false;
        }
    }

    /**
     * 指定缓存失效时间
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(getRedisFullKey(key), time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("redis expire exception", e);
            return false;
        }
    }


    public interface Operator<T> {
        T execute();
    }

    /**
     * 取完整的key值
     */
    private String getRedisFullKey(String key) {
        String redis_prefix = "PAYSPHERE_";

        if (key.startsWith(redis_prefix)) {
            return key;
        }
        return redis_prefix + key;
    }

}