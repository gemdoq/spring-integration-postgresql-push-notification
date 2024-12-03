package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SmtpMailSenderTest {

	@Mock
	private JavaMailSender javaMailSender;

	@InjectMocks
	private SmtpMailSender smtpMailSender;

	@Test
	@DisplayName("sendMail method should send an email successfully")
	void testSendMail() throws Exception {
		ReflectionTestUtils.setField(smtpMailSender, "MAIL_USERNAME", "from@email.com");

		MailMessage mailMessage = new MailMessage("Test Subject", "<h1>HTML Content</h1>", "testSendMail@email.com");
		MimeMessage mimeMessage = Mockito.mock(MimeMessage.class);

		when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

		smtpMailSender.sendMail(mailMessage);

		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(1)).send(mimeMessage);
	}

	@Test
	@DisplayName("sendMail method should throw MailSendException on failure")
	void testSendMailFailure() {
		MailMessage mailMessage = new MailMessage("Test Subject", "<h1>HTML Content</h1>", "testSendMailFailure@email.com");

		when(javaMailSender.createMimeMessage()).thenThrow(new RuntimeException("Mail server error"));

		assertThrows(MailSendException.class, () -> smtpMailSender.sendMail(mailMessage));
		verify(javaMailSender, times(1)).createMimeMessage();
		verify(javaMailSender, times(0)).send(Mockito.any(MimeMessage.class));
	}
}