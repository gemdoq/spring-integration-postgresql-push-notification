package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.service.MailLogService;
import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

import java.time.Duration;

@Configuration
public class MailConfiguration {
	private final OutboxRepository outboxRepository;
	private final RequestHandlerRetryAdvice retryAdvice;

	@Value("${MAIL_USERNAME}")
	private String MAIL_USERNAME;

	public MailConfiguration(
			OutboxRepository outboxRepository,
			@Qualifier("retryAdvice") RequestHandlerRetryAdvice retryAdvice
	) {
		this.outboxRepository = outboxRepository;
		this.retryAdvice = retryAdvice;
	}
	@Bean
	public DirectChannel inbox() {
		return new DirectChannel();
	}

	@Bean
	public QueueChannel outbox(JdbcChannelMessageStore store) {
		QueueChannel channel = MessageChannels.queue(store, "mail-outbox").getObject();
		channel.addInterceptor(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				System.out.println("========================");
				System.out.println("Message Headers: " + message.getHeaders());

				Object messageId = message.getHeaders().get("MESSAGE_ID");
				Object createdDate = message.getHeaders().get("CREATED_DATE");

				System.out.println("MESSAGE_ID: " + messageId);
				System.out.println("MESSAGE_ID 타입: " + (messageId != null ? messageId.getClass() : "null"));
				System.out.println("CREATED_DATE: " + createdDate);
				System.out.println("CREATED_DATE 타입: " + (createdDate != null ? createdDate.getClass() : "null"));
				System.out.println("========================");
				return message;
			}
		});
		return channel;
	}

	@Bean
	public IntegrationFlow mailFlow(JdbcChannelMessageStore store, MailSender mailSender, MailLogService mailLogService) {
		return IntegrationFlow.from(inbox())
				.log(LoggingHandler.Level.INFO, message -> "수신된 메시지: " + message)
				.channel(outbox(store))
				.handle(message -> {
					MailMessage mail = (MailMessage) message.getPayload();
					String messageId = message.getHeaders().get("MESSAGE_ID").toString();
					try {
						mailSender.sendMail(mail);	// 메일 전송 시도
						outboxRepository.updateStatus(messageId, "PROCESSED");	// 성공 시 상태 업데이트
						System.out.println("메시지가 성공적으로 처리되었습니다" + messageId);
						mailLogService.saveMailLog(MAIL_USERNAME, mail.to(), mail.subject(), mail.body()); // 메일 전송 성공 시 메일로그 저장
					} catch (MailSendException e) {
						System.out.println("메일 전송 에러 발생: " + e.getMessage());
						// 재시도 실패 시만 상태 업데이트
						throw new RuntimeException(e);					}
				}, e -> e.advice(retryAdvice) // 재시도 로직 추가
						.poller(Pollers.fixedDelay(Duration.ofSeconds(1)).maxMessagesPerPoll(10).transactional()))
				.get();
	}
}
