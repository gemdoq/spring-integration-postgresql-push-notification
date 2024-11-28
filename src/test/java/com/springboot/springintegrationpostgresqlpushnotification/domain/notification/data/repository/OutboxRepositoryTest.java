package com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository;

import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.entity.Outbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OutboxRepositoryTest {

	@Autowired
	private OutboxRepository outboxRepository;

	@Test
	@DisplayName("Is the outbox being saved correctly through the repository")
	public void testSaveOutbox() {
		String uuid = UUID.randomUUID().toString();
		Outbox outbox = new Outbox(uuid, "ORDER_CREATED", "{\"orderId\":\"12345\"}", "PENDING");
		Outbox savedOutbox = outboxRepository.save(outbox);

		assertNotNull(savedOutbox.getId());
		assertEquals(uuid, savedOutbox.getId());
		assertEquals("ORDER_CREATED", savedOutbox.getEventType());
		assertEquals("{\"orderId\":\"12345\"}", savedOutbox.getPayload());
		assertEquals("PENDING", savedOutbox.getStatus());
		assertNotNull(savedOutbox.getCreatedDttm());
	}

	@Test
	@DisplayName("Is the outbox being retrieved correctly through the repository by status")
	public void testFindByStatus() {
		Outbox outbox1 = new Outbox(UUID.randomUUID().toString(), "ORDER_CREATED", "{\"orderId\":\"12345\"}", "PENDING");
		Outbox outbox2 = new Outbox(UUID.randomUUID().toString(), "ORDER_UPDATED", "{\"orderId\":\"54321\"}", "FAILED");
		outboxRepository.saveAll(List.of(outbox1, outbox2));

		List<Outbox> pendingOutboxes = outboxRepository.findByStatus("PENDING");

		assertEquals(1, pendingOutboxes.size());
		assertEquals("ORDER_CREATED", pendingOutboxes.getFirst().getEventType());
		assertEquals("{\"orderId\":\"12345\"}", pendingOutboxes.getFirst().getPayload());
		assertEquals("PENDING", pendingOutboxes.getFirst().getStatus());
		assertNotNull(pendingOutboxes.getFirst().getCreatedDttm());
	}
}
