package com.springboot.springintegrationpostgresqlpushnotification.global.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryTemplateConfig {

	@Bean
	public RetryTemplate retryTemplate() {
		RetryTemplate retryTemplate = new RetryTemplate();

		// 재시도 정책 설정 (최대 재시도 횟수 3)
		SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
		retryPolicy.setMaxAttempts(3);

		// 백오프 정책 설정 (재시도 간격 2초)
		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(2000);

		retryTemplate.setRetryPolicy(retryPolicy);
		retryTemplate.setBackOffPolicy(backOffPolicy);

		return retryTemplate;
	}
}
