package com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.service;

import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.entity.MailLog;
import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.repository.MailLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class MailLogServiceTest {

	@Mock
	private MailLogRepository mailLogRepository;

	@InjectMocks
	private MailLogService mailLogService;

	@Test
	@DisplayName("save mailLog and verify repository invocation")
	public void testSaveMailLog() {
		// given
		String sender = "sender@example.com";
		String recipient = "recipient@example.com";
		String subject = "test subject";
		String body = "test body of the email";
		LocalDateTime sentAt = LocalDateTime.now();

		MailLog mailLog = MailLog.builder()
				.sender(sender)
				.recipient(recipient)
				.subject(subject)
				.body(body)
				.sentAt(sentAt)
				.build();

		// mock save method of repository
		Mockito.when(mailLogRepository.save(Mockito.any(MailLog.class))).thenReturn(mailLog);

		// when
		mailLogService.saveMailLog(sender, recipient, subject, body);

		// then
		ArgumentCaptor<MailLog> mailLogCaptor = ArgumentCaptor.forClass(MailLog.class);
		Mockito.verify(mailLogRepository, Mockito.times(1)).save(mailLogCaptor.capture());

		MailLog capturedMailLog = mailLogCaptor.getValue();
		assertThat(capturedMailLog.getSender()).isEqualTo(sender);
		assertThat(capturedMailLog.getRecipient()).isEqualTo(recipient);
		assertThat(capturedMailLog.getSubject()).isEqualTo(subject);
		assertThat(capturedMailLog.getBody()).isEqualTo(body);
		assertThat(capturedMailLog.getSentAt()).isNotNull();
	}
}