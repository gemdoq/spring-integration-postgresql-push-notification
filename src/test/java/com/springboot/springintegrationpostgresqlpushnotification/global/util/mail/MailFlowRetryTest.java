package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.service.MailLogService;
import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MailFlowRetryTest {

	@MockBean
	private MailSender mailSender;

	@MockBean
	private OutboxRepository outboxRepository;

	@MockBean
	private MailLogService mailLogService;

	@Autowired
	private MessageChannel inbox;

	@Value("${MAIL_USERNAME}")
	private String MAIL_USERNAME;

	@Test
	@DisplayName("Test if retries occur when email transmission fails")
	public void testMailRetryAndFailureHandling() throws Exception {
		// Given: Mock된 Outbox와 MailSender
		String messageId = "test-message-id";
		MailMessage testMail = new MailMessage("Test Subject", "Test Body", "test@example.com");

		Message<?> testMessage = MessageBuilder.withPayload(testMail)
				.setHeader("MESSAGE_ID", messageId)
				.build();

		// Mock: 메일 전송 실패를 시뮬레이션
		doThrow(new MailSendException("메일 전송 실패"))
				.when(mailSender).sendMail(testMail);

		// Mock: Outbox 상태 업데이트
		doNothing().when(outboxRepository).updateStatus(eq(messageId), anyString());

		// When: 메시지를 Inbox 채널로 전송
		inbox.send(testMessage);

		// Wait: 재시도 간격 및 실행 시간 대기
		Thread.sleep(10000); // 10초 대기 (재시도 처리 시간 포함)

		// Then: 최종 상태 확인
		verify(outboxRepository, times(1)).updateStatus(messageId, "FAILED"); // 최종적으로 한 번 호출됨
		verify(mailSender, times(3)).sendMail(testMail); // 재시도 횟수 검증
	}

	@Test
	@DisplayName("Test if email transmission succeeds on retry")
	public void testMailRetryAndSuccessHandling() throws Exception {
		// Given: Mock된 Outbox와 MailSender
		String messageId = "success-message-id";
		MailMessage testMail = new MailMessage("Success Subject", "Success Body", "success@example.com");

		Message<?> testMessage = MessageBuilder.withPayload(testMail)
				.setHeader("MESSAGE_ID", messageId)
				.build();

		// Mock: 메일 전송 성공
		doNothing().when(mailSender).sendMail(testMail);

		// Mock: Outbox 상태 업데이트
		doNothing().when(outboxRepository).updateStatus(eq(messageId), anyString());

		// When: 메시지를 Inbox 채널로 전송
		inbox.send(testMessage);

		// Wait: 성공 처리 시간 대기
		Thread.sleep(2000);

		// Then: 최종 상태 확인
		verify(outboxRepository, times(1)).updateStatus(messageId, "PROCESSED");
		verify(mailSender, times(1)).sendMail(testMail); // 한 번만 호출되어야 함

		// 메일 로그 저장 검증
		verify(mailLogService, times(1))
				.saveMailLog(eq(MAIL_USERNAME), eq(testMail.to()), eq(testMail.subject()), eq(testMail.body()));
	}

}