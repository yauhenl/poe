package com.yauhenl.poe.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
@EnableAsync
public class JobConfiguration {

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        int processors = Runtime.getRuntime().availableProcessors();
        executor.setCorePoolSize(processors + 1);
        executor.setMaxPoolSize(processors * 2);
        executor.setQueueCapacity((int) (processors * 2.5));
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler result = new ThreadPoolTaskScheduler();
        result.setPoolSize(Runtime.getRuntime().availableProcessors() + 1);
        return result;
    }
}
