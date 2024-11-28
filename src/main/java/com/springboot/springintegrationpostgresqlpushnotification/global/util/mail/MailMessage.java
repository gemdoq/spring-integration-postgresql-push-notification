package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import java.io.Serializable;

public record MailMessage(String subject, String body, String to) implements Serializable {
	private static final long serialVersionUID = 1L;
}
