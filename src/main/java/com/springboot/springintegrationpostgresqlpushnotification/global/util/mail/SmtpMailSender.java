package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
public class SmtpMailSender implements MailSender {
	private final JavaMailSender javaMailSender;

	@Value("${MAIL_USERNAME}")
	private String MAIL_USERNAME;

	public SmtpMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void sendMail(MailMessage mailMessage) throws MailSendException {
		try {
			SimpleMailMessage message = new SimpleMailMessage();
			message.setTo(mailMessage.to());
			message.setSubject(mailMessage.subject());
			message.setText(mailMessage.body());
			message.setFrom(MAIL_USERNAME);

			javaMailSender.send(message);	// JavaMailSender 메일 전송
			System.out.println("메일 전송 성공: " + mailMessage.to());
		} catch (Exception e) {
			throw new MailSendException("메일 전송 실패: " + e.getMessage(), e);
		}
	}
}
