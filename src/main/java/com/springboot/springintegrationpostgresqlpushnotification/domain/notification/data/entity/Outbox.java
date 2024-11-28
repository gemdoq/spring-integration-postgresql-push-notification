package com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tb_outbox")
public class Outbox {
	@Id
	private String id;
	private String eventType;
	private String payload;
	private String status;
	private LocalDateTime createdDttm = LocalDateTime.now();

	public Outbox(String id, String eventType, String payload, String status) {
		this.id = id;
		this.eventType = eventType;
		this.payload = payload;
		this.status = status;
	}

}
