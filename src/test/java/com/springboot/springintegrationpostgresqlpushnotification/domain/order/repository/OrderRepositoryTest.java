package com.springboot.springintegrationpostgresqlpushnotification.domain.order.repository;

import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.entity.Order;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class OrderRepositoryTest {

	@Autowired
	private OrderRepository orderRepository;

	@Test
	@DisplayName("Is the order being saved correctly through the repository")
	public void testSaveOrder() {
		Order orderToSave = new Order();
		orderToSave.setAmount(BigDecimal.valueOf(100.0));
		orderToSave.setCustomerEmail("testSaveOrder@email.com");

		Order savedOrder = orderRepository.save(orderToSave);

		assertNotNull(savedOrder.getId());
		assertEquals(BigDecimal.valueOf(100.0), savedOrder.getAmount());
		assertEquals("testSaveOrder@email.com", savedOrder.getCustomerEmail());
	}

	@Test
	@DisplayName("Is the order being retrieved correctly through the repository by id")
	public void testFindById() {
		Order orderToSave = new Order();
		orderToSave.setAmount(BigDecimal.valueOf(200.0));
		orderToSave.setCustomerEmail("testFindById@email.com");

		Order savedOrder = orderRepository.save(orderToSave);

		Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());

		assertNotNull(foundOrder.get());
		assertEquals(BigDecimal.valueOf(200.0), foundOrder.get().getAmount());
		assertEquals("testFindById@email.com", foundOrder.get().getCustomerEmail());
	}
}