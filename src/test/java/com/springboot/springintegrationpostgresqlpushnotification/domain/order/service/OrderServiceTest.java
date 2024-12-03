package com.springboot.springintegrationpostgresqlpushnotification.domain.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository.OutboxRepository;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.entity.Order;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.repository.OrderRepository;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.MailGateway;
import com.springboot.springintegrationpostgresqlpushnotification.global.util.mail.MailMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.Message;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class OrderServiceTest {

	@Autowired
	private OrderService orderService;

	@MockBean
	private OrderRepository orderRepository;

	@MockBean
	private OutboxRepository outboxRepository;

	@MockBean
	private MailGateway mailGateway;

	@MockBean
	private SpringTemplateEngine templateEngine;

	@Test
	@DisplayName("is saved in the repository and the email is sent correctly")
	public void testSaveOrderSendsEmail() throws JsonProcessingException {
		BigDecimal amount = BigDecimal.valueOf(100.0);
		String email = "testSaveOrderSendsEmail@email.com";
		Order order = new Order(amount, email);

		when(orderRepository.save(any(Order.class))).thenReturn(order);

		orderService.saveOrder(amount, email);

		verify(outboxRepository, times(1)).save(argThat(outbox ->
				outbox.getEventType().equals("ORDER_CREATED") &&
						outbox.getStatus().equals("PENDING") && outbox.getPayload().contains("\"amount\":100.0")
		));

		verify(orderRepository, times(1)).save(any(Order.class));
		verify(mailGateway, times(1)).enqueueMailEvent(any(Message.class));
	}

	@Test
	@DisplayName("is saved in the repository and the email is sent correctly with rendered template")
	public void testSaveOrder2SendsEmailWithTemplate() throws JsonProcessingException {
		BigDecimal amount = BigDecimal.valueOf(200.0);
		String email = "testSaveOrder2SendsEmailWithTemplate@email.com";
		String name = "testname";
		String renderedHtml = "<html><body>주문 성공 본문</body></html>";

		Order order = new Order(amount, email);

		when(orderRepository.save(any(Order.class))).thenReturn(order);
		when(templateEngine.process(eq("order-success-template"), any(Context.class))).thenReturn(renderedHtml);

		orderService.saveOrder2(name, amount, email);

		verify(outboxRepository, times(1)).save(argThat(outbox ->
				outbox.getEventType().equals("ORDER_CREATED") &&
						outbox.getStatus().equals("PENDING") && outbox.getPayload().contains("\"amount\":200.0")
		));

		verify(orderRepository, times(1)).save(any(Order.class));
		verify(templateEngine, times(1)).process(eq("order-success-template"), any(Context.class));
		verify(mailGateway, times(1)).enqueueMailEvent(argThat(message ->
				message.getPayload() instanceof MailMessage &&
						((MailMessage) message.getPayload()).body().equals(renderedHtml)
		));
	}
}