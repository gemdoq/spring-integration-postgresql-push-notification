package com.springboot.springintegrationpostgresqlpushnotification.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.entity.Outbox;
import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.entity.Order;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.repository.OrderRepository;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.MailGateway;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.MailMessage;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.MailMessageGenerator;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.ParamUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@Transactional
public class OrderService {

	// 해당 TEMPLATE_NAME은 사용하고자 하는 도메인 비지니스 로직에 따라 적절히 변경
	private String TEMPLATE_NAME = "order-success-template";

	private final OrderRepository orderRepository;
	private final OutboxRepository outboxRepository;
	private final MailGateway mailGateway;
	private SpringTemplateEngine templateEngine;
	private MailMessageGenerator mailMessageGenerator;

	public OrderService(OrderRepository orderRepository, OutboxRepository outboxRepository, MailGateway mailGateway, SpringTemplateEngine templateEngine, MailMessageGenerator mailMessageGenerator) {
		this.orderRepository = orderRepository;
		this.outboxRepository = outboxRepository;
		this.mailGateway = mailGateway;
		this.templateEngine = templateEngine;
		this.mailMessageGenerator = mailMessageGenerator;
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

	public void saveOrder2(String name, BigDecimal amount, String email) throws JsonProcessingException {
		// create order
		Order order = new Order();

		// set prop of order
		order.setAmount(amount);
		order.setCustomerEmail(email);

		// save order
		Order savedOrder = orderRepository.save(order);

		// create outbox
		String uuid = UUID.randomUUID().toString();
		Outbox outbox = new Outbox(uuid, "ORDER_CREATED", new ObjectMapper().writeValueAsString(savedOrder), "PENDING");

		// save outbox
		outboxRepository.save(outbox);

		// create context(ctx)
		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("amount", amount);
		context.setVariable("email", email);

		// process html template through template engine with ctx
		String htmlStringContent = templateEngine.process("order-success-template", context);
		System.out.println("======================");
		System.out.println("Template Engine에 의해 처리된 htmlStringContent: " + htmlStringContent);
		System.out.println("======================");

		// create MailMessage(order's ID, ctx rendered html string, email)
		MailMessage mailMessage = new MailMessage(
				"%s번 주문 처리 성공".formatted(savedOrder.getId()),
				"%s".formatted(htmlStringContent),
				email
		);

		// create message
		Message<?> message = MessageBuilder.withPayload(mailMessage)
				.setHeader("MESSAGE_ID", uuid)
				.setHeader("CREATED_DATE", Timestamp.valueOf(LocalDateTime.now()))
				.build();

		// enqueue message
		mailGateway.enqueueMailEvent(message);
	}

	public void saveOrder3(String name, BigDecimal amount, String email) throws IOException {
		// create order
		Order order = new Order();

		// set prop of order
		order.setAmount(amount);
		order.setCustomerEmail(email);

		// save order
		Order savedOrder = orderRepository.save(order);

		// create outbox
		String uuid = UUID.randomUUID().toString();
		Outbox outbox = new Outbox(uuid, "ORDER_CREATED", new ObjectMapper().writeValueAsString(savedOrder), "PENDING");

		// save outbox
		outboxRepository.save(outbox);

		// create map contains all params of this method to fill in the email template to be sent
		Map<String, Object> domainMap = ParamUtils.addAndGetParams(name, amount, email);
		// add the data want to include in the email to be sent

		// generate mailMessage
		MailMessage genMailMessage = mailMessageGenerator.generateMailMessage(
				"%s번 주문 처리 성공".formatted(savedOrder.getId()),
				TEMPLATE_NAME,  // html template filename written according to biz logic
				domainMap,  // data required for the message to be sent via email
				email
		);

		// create message
		Message<?> message = MessageBuilder.withPayload(genMailMessage)
				.setHeader("MESSAGE_ID", uuid)
				.setHeader("CREATED_DATE", Timestamp.valueOf(LocalDateTime.now()))
				.build();

		// enqueue message
		mailGateway.enqueueMailEvent(message);
	}
}
