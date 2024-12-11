package com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.service;

import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.entity.MailLog;
import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.repository.MailLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class MailLogService {

	private final MailLogRepository mailLogRepository;

	public MailLogService(MailLogRepository mailLogRepository) {
		this.mailLogRepository = mailLogRepository;
	}

	@Transactional
	public void saveMailLog(String sender, String recipient, String subject, String body) {
		MailLog mailLog = MailLog.builder()
				.sender(sender)
				.recipient(recipient)
				.subject(subject)
				.body(body)
				.sentAt(LocalDateTime.now())
				.build();
		mailLogRepository.save(mailLog);
	}
}
