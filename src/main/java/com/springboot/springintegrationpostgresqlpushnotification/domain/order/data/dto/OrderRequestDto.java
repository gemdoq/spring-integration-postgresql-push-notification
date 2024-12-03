package com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@EqualsAndHashCode
public class OrderRequestDto {
	private BigDecimal amount;
	private String email;
}
