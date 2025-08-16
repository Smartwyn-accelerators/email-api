package com.fastcode.emailApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Autowired
    private EmailApiPropertiesConfiguration env;

    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(env.getEmailThreadPoolCorePoolSize());
        executor.setMaxPoolSize(env.getEmailThreadPoolMaxPoolSize());
        executor.setQueueCapacity(env.getEmailThreadPoolQueueCapacity());
        executor.setKeepAliveSeconds(env.getEmailThreadPoolKeepAliveSeconds());
        executor.setThreadNamePrefix(env.getEmailThreadPoolThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
