package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.Message;

@MessagingGateway
public interface MailGateway {
	@Gateway(requestChannel = "inbox")
	void enqueueMailEvent(Message<?> message);
}
