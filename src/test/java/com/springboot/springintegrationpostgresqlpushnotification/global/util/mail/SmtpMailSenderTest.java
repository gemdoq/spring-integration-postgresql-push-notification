package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class SmtpMailSenderTest {

	private final JavaMailSender javaMailSender = Mockito.mock(JavaMailSender.class);
	private final SmtpMailSender smtpMailSender = new SmtpMailSender(javaMailSender);

	@Test
	@DisplayName("Is the sendMail method sending an email successfully?")
	void testSendMail() {
		MailMessage mailMessage = new MailMessage("Test Subject", "Test Body", "test@example.com");
		smtpMailSender.sendMail(mailMessage);
		verify(javaMailSender, times(1)).send(Mockito.any(SimpleMailMessage.class));
	}
}