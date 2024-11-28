package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

public interface MailSender {
	void sendMail(MailMessage mailMessage) throws MailSendException;
}
