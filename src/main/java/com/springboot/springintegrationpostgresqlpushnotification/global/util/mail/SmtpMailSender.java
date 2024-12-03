package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

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
			MimeMessage mimeMessage = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

			helper.setTo(mailMessage.to());
			helper.setSubject(mailMessage.subject());
			helper.setText(mailMessage.body(), true);
			helper.setFrom(MAIL_USERNAME);

			javaMailSender.send(mimeMessage);
			System.out.println("메일 전송 성공: " + mailMessage.to());
		} catch (Exception e) {
			throw new MailSendException("메일 전송 실패: " + e.getMessage(), e);
		}
	}
}
