package com.paysphere.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 */

@Configuration
@EnableAsync
public class TreadPoolConfig {

    @Value("${thread.pool.corePoolSize:128}")
    int corePoolSize;

    @Value("${thread.pool.maxPoolSize:128}")
    int maxPoolSize;

    @Value("${thread.pool.queueCapacity:1024}")
    int queueCapacity;

    @Value("${thread.pool.keepAliveSeconds:60}")
    int keepAliveSeconds;

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setCorePoolSize(corePoolSize);
        pool.setMaxPoolSize(maxPoolSize);
        pool.setQueueCapacity(queueCapacity);
        pool.setKeepAliveSeconds(keepAliveSeconds);
        pool.setThreadNamePrefix("trade-executor-");
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 没有设置下面参数，在kill -15时，线程池没有执行结束，会被强制关闭
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setAwaitTerminationSeconds(30);
        pool.initialize();
        return pool;
    }

    /**
     * 异步任务中异常处理
     */
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}

