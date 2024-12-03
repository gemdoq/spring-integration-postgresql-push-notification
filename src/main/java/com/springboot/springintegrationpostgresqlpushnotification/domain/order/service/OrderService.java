package com.springboot.springintegrationpostgresqlpushnotification.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.entity.Outbox;
import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.entity.Order;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.repository.OrderRepository;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.MailGateway;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.MailMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Transactional
public class OrderService {
	private final OrderRepository orderRepository;
	private final OutboxRepository outboxRepository;
	private final MailGateway mailGateway;

	public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository, MailGateway mailGateway) {
		this.orderRepository = orderRepository;
		this.outboxRepository = outboxRepository;
		this.mailGateway = mailGateway;
	}

	public void saveOrder(BigDecimal amount, String email) throws JsonProcessingException {
		Order order = new Order();
		order.setAmount(amount);
		order.setCustomerEmail(email);

		Order savedOrder = orderRepository.save(order);

		String uuid = UUID.randomUUID().toString();
		Outbox outbox = new Outbox(uuid, "ORDER_CREATED", new ObjectMapper().writeValueAsString(savedOrder), "PENDING");
		outboxRepository.save(outbox);

		MailMessage mailMessage = new MailMessage(
				"%s번 주문 완료".formatted(savedOrder.getId()),
				"주문이 성공했습니다. 곧 처리됩니다.",
				email
		);

		Message<?> message = MessageBuilder.withPayload(mailMessage)
				.setHeader("MESSAGE_ID", uuid)
				.setHeader("CREATED_DATE", Timestamp.valueOf(LocalDateTime.now()))
				.build();

		mailGateway.enqueueMailEvent(message);
	}
}