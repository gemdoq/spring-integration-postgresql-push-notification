package com.springboot.springintegrationpostgresqlpushnotification.domain.notification.service;

import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;

import static org.mockito.Mockito.*;

@SpringBootTest
public class OutboxCleanupTest {

	@MockBean
	private OutboxRepository outboxRepository;

	@Autowired
	private OutboxCleanup outboxCleanup;

	@BeforeEach
	public void setup() {
		Mockito.reset(outboxRepository);
	}

	@Test
	@DisplayName("Verify old FAILED messages are cleaned up correctly")
	public void testCleanupOldFailedMessages() {
		// Given
		when(outboxRepository.deleteOldFailedMessages(any(Timestamp.class))).thenReturn(5);

		// When
		outboxCleanup.cleanupOldFailedMessages();

		// Then
		verify(outboxRepository, times(1)).deleteOldFailedMessages(any(Timestamp.class));
	}
}