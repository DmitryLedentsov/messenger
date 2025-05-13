package com.dimka228.messenger.config;

import java.util.concurrent.Executor;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.dimka228.messenger.config.properties.TaskExecutorProperties;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableAsync 
@ConditionalOnProperty(name = "messenger.async.enabled", havingValue = "true", matchIfMissing = false)
@Slf4j
public class AsyncConfig {

    private final TaskExecutorProperties properties;

    public AsyncConfig(TaskExecutorProperties properties) {
        log.info("Async enabled: {}", properties);
        this.properties = properties;
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCorePoolSize());
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.initialize();
        return executor;
    }
}