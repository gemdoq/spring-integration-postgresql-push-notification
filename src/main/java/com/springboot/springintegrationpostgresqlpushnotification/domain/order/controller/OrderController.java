package com.springboot.springintegrationpostgresqlpushnotification.domain.order.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.dto.OrderRequestDto;
import com.springboot.springintegrationpostgresqlpushnotification.domain.order.service.OrderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping("/new")
	public void acceptOrder(@RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
		BigDecimal amount = orderRequestDto.getAmount();
		String email = orderRequestDto.getEmail();
		orderService.saveOrder(amount, email);
	}

	@PostMapping("/new2")
	public void acceptOrder2(@RequestBody OrderRequestDto orderRequestDto) throws JsonProcessingException {
		BigDecimal amount = orderRequestDto.getAmount();
		String email = orderRequestDto.getEmail();
		String name = email.substring(0, email.indexOf("@"));
		orderService.saveOrder2(name, amount, email);
	}
}
