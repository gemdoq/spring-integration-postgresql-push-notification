package com.springboot.springintegrationpostgresqlpushnotification.domain.notification.service;

import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class OutboxCleanup {
	private final OutboxRepository outboxRepository;

	public OutboxCleanup(OutboxRepository outboxRepository) {
		this.outboxRepository = outboxRepository;
	}

	@Scheduled(cron = "0 0 2 * * ?") // 매일 새벽 2시에 실행
	@Transactional
	public void cleanupOldFailedMessages() {
		int deletedCount = outboxRepository.deleteOldFailedMessages(
				Timestamp.valueOf(LocalDateTime.now().minusDays(30))
		);
		System.out.println("오래된 실패 메시지 삭제 완료. 삭제된 메시지 수: " + deletedCount);
	}
}