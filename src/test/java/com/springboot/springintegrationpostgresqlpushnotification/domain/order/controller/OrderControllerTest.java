package com.springboot.springintegrationpostgresqlpushnotification.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.dto.OrderRequestDto;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private OrderService orderService;

	@Test
	@DisplayName("is saved correctly through using the order service")
	public void testAcceptOrder() throws Exception {
		BigDecimal amount = BigDecimal.valueOf(100.0);
		String email = "testAcceptOrder@email.com";

		OrderRequestDto orderRequestDto = OrderRequestDto.builder()
				.amount(amount)
				.email(email)
				.build();

		mockMvc.perform(post("/api/v1/order/new")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderRequestDto)))
				.andExpect(status().isOk());

		verify(orderService, times(1)).saveOrder(any(BigDecimal.class), any(String.class));
	}

	@Test
	@DisplayName("acceptOrder2 calls saveOrder2 with the correct arguments")
	public void testAcceptOrder2() throws Exception {
		BigDecimal amount = BigDecimal.valueOf(200.0);
		String email = "testAcceptOrder2@email.com";

		OrderRequestDto orderRequestDto = OrderRequestDto.builder()
				.amount(amount)
				.email(email)
				.build();

		mockMvc.perform(post("/api/v1/order/new2")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(orderRequestDto)))
				.andExpect(status().isOk());

		String expectedName = email.substring(0, email.indexOf("@"));

		verify(orderService, times(1)).saveOrder2(eq(expectedName), eq(amount), eq(email));
	}
}
