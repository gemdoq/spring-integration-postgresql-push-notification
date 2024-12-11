package com.springboot.springintegrationpostgresqlpushnotification.global.common.config;

import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.messaging.Message;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryAdviceConfig {
	private final OutboxRepository outboxRepository;
	private final RetryTemplate retryTemplate;

	public RetryAdviceConfig(OutboxRepository outboxRepository, @Qualifier("retryTemplate") RetryTemplate retryTemplate) {
		this.outboxRepository = outboxRepository;
		this.retryTemplate = retryTemplate;
	}

	// Spring Retry를 위한 Advice 추가
	@Bean
	public RequestHandlerRetryAdvice retryAdvice() {
		RequestHandlerRetryAdvice retryAdvice = new RequestHandlerRetryAdvice();
		retryAdvice.setRetryTemplate(retryTemplate); // 설정된 빈 사용
		retryAdvice.setRecoveryCallback(context -> {
			// 최대 재시도 실패 후 처리 로직
			Message<?> failedMessage = (Message<?>) context.getAttribute("message");
			String messageId = (String) failedMessage.getHeaders().get("MESSAGE_ID");

			// Outbox 상태를 'FAILED'로 업데이트
			if (messageId != null) {
				outboxRepository.updateStatus(messageId, "FAILED"); // 재시도 모두 실패 시 상태 업데이트
			}

			System.err.println("최대 재시도 횟수를 초과하여 실패: MESSAGE_ID=" + messageId);
			return null;
		});
		return retryAdvice;
	}
}
