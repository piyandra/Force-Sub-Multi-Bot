package org.telegram.forcesubmultibot.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class ThreadPoolConfig {

	@Bean(name = "webhookExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
		poolTaskExecutor.setCorePoolSize(10);
		poolTaskExecutor.setMaxPoolSize(100);
		poolTaskExecutor.setQueueCapacity(1000);
		poolTaskExecutor.setThreadNamePrefix("ForceSubMultiBot-");
		return new ThreadPoolTaskExecutor();
	}

	@Bean(name = "service")
	public Executor serviceExecutor() {
		ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
		poolTaskExecutor.setCorePoolSize(10);
		poolTaskExecutor.setMaxPoolSize(20);
		poolTaskExecutor.setQueueCapacity(2000);
		poolTaskExecutor.setThreadNamePrefix("ServiceExecutor-");
		return new ThreadPoolTaskExecutor();
	}

	@Bean(name = "handle")
	public Executor handleExecutor() {
		ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
		poolTaskExecutor.setCorePoolSize(30);
		poolTaskExecutor.setMaxPoolSize(300);
		poolTaskExecutor.setQueueCapacity(2000);
		poolTaskExecutor.setThreadNamePrefix("ServiceExecutor-");
		return new ThreadPoolTaskExecutor();
	}
}
