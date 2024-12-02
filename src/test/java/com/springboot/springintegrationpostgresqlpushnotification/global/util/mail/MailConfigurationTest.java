package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MailConfigurationTest {

	@Autowired
	private DirectChannel inbox;

	@Autowired
	private QueueChannel outbox;

	@Mock
	private MailSender mailSender;

	@Autowired
	private OutboxRepository outboxRepository;

	@Autowired
	private MailGateway mailGateway;

	@InjectMocks
	private MailConfiguration mailConfiguration;

	private MailMessage mailMessage;
	private Message<MailMessage> message;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		mailMessage = new MailMessage("Test Subject", "Test Body", "test@example.com");
		message = MessageBuilder
				.withPayload(mailMessage)
				.setHeader("MESSAGE_ID", UUID.randomUUID().toString())
				.build();
	}

	@Test
	@DisplayName("are mail messages successfully enqueued into the outbox queue")
	void testMailEnqueue() {
		mailGateway.enqueueMailEvent(message);

		Message<?> received = outbox.receive(0);
		assertThat(received).isNotNull();
		assertThat(received.getPayload()).isInstanceOf(MailMessage.class);
		MailMessage receivedMail = (MailMessage) received.getPayload();
		assertThat(receivedMail.subject()).isEqualTo("Test Subject");
		assertThat(receivedMail.body()).isEqualTo("Test Body");
		assertThat(receivedMail.to()).isEqualTo("test@example.com");
	}

	@Test
	@DisplayName("is entire mail processing integration flow correctly inbox to outbox")
	void testIntegrationFlow() {
		inbox.send(message);

		Message<?> receivedMessage = outbox.receive(0);
		assert receivedMessage != null;

		MailMessage receivedMail = (MailMessage) receivedMessage.getPayload();
		assert receivedMail.subject().equals("Test Subject");
		assert receivedMail.body().equals("Test Body");
		assert receivedMail.to().equals("test@example.com");

		Mockito.doNothing().when(mailSender).sendMail(receivedMail);
	}
}
