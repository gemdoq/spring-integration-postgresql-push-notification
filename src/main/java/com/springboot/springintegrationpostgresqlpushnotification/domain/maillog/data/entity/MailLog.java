package com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_mail_log")
public class MailLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String sender;  // 보낸 사람 이메일
	private String recipient;  // 받는 사람 이메일
	private String subject;  // 메일 제목
	@Lob
	@Column(columnDefinition = "TEXT")
	private String body; // 메일 본문
	private LocalDateTime sentAt;  // 전송 시간
}