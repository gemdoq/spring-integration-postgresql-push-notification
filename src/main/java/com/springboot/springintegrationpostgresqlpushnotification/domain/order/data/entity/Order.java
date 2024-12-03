package com.springboot.springintegrationpostgresqlpushnotification.domain.order.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_order")
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private BigDecimal amount;

	@Column(name = "customer_email")
	private String customerEmail;

	public Order(BigDecimal amount, String email) {
		this.amount = amount;
		this.customerEmail = email;
	}
}