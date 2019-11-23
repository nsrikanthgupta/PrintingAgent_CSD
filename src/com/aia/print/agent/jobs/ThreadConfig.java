/* Project Print Agent
 * All rights reserved for AIA.
 */

package com.aia.print.agent.jobs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * TODO: please describe responsibilities of class/interface
 * 
 * 
 * @author Srikanth Neerumalla
 * @DateTime 23 Nov 2019 10:25:28 am
 */
@Configuration
@EnableAsync
public class ThreadConfig {

    /**
     * corerPoolSize
     */
    @Value("${print.agent.thread.pool.core.size}")
    private Integer corerPoolSize;

    /**
     * maxPoolSize
     */
    @Value("${print.agent.thread.pool.max.size}")
    private Integer maxPoolSize;

    /**
     * Configure the Thread Pool Task Executor
     * 
     * @return {@link TaskExecutor}
     */
    @Bean
    public TaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corerPoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setThreadNamePrefix("default_task_executor_thread_");
        executor.initialize();
        return executor;
    }

    /**
     * Returns the corerPoolSize.
     * 
     * @return the corerPoolSize.
     */
    public Integer getCorerPoolSize() {
        return corerPoolSize;
    }

    /**
     * Sets the corerPoolSize.
     * 
     * @param corerPoolSize the corerPoolSize
     */
    public void setCorerPoolSize(Integer corerPoolSize) {
        this.corerPoolSize = corerPoolSize;
    }

    /**
     * Returns the maxPoolSize.
     * 
     * @return the maxPoolSize.
     */
    public Integer getMaxPoolSize() {
        return maxPoolSize;
    }

    /**
     * Sets the maxPoolSize.
     * 
     * @param maxPoolSize the maxPoolSize
     */
    public void setMaxPoolSize(Integer maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }
}
