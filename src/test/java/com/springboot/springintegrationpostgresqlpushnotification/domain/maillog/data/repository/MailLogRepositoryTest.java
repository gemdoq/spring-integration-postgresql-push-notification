package com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.repository;

import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.entity.MailLog;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
public class MailLogRepositoryTest {

	@Autowired
	private MailLogRepository mailLogRepository;

	@Test
	@DisplayName("save a mailLog and verify if it is stored correctly")
	public void testSaveMailLog() {
		// Given
		MailLog mailLog = MailLog.builder()
				.sender("sender@example.com")
				.recipient("recipient@example.com")
				.subject("test subject")
				.body("this is a test email body")
				.sentAt(LocalDateTime.now())
				.build();

		// When
		MailLog savedMailLog = mailLogRepository.save(mailLog);

		// Then
		assertThat(savedMailLog.getId()).isNotNull();
		assertThat(savedMailLog.getSender()).isEqualTo("sender@example.com");
		assertThat(savedMailLog.getRecipient()).isEqualTo("recipient@example.com");
		assertThat(savedMailLog.getSubject()).isEqualTo("test subject");
		assertThat(savedMailLog.getBody()).isEqualTo("this is a test email body");
		assertThat(savedMailLog.getSentAt()).isNotNull();
	}

	@Test
	@DisplayName("find a mailLog by id and verify the retrieved data")
	public void testFindById() {
		// Given
		MailLog mailLog = MailLog.builder()
				.sender("sender@example.com")
				.recipient("recipient@example.com")
				.subject("find test")
				.body("finding this email log by id")
				.sentAt(LocalDateTime.now())
				.build();
		MailLog savedMailLog = mailLogRepository.save(mailLog);

		// When
		Optional<MailLog> foundMailLog = mailLogRepository.findById(savedMailLog.getId());

		// Then
		assertThat(foundMailLog).isPresent();
		assertThat(foundMailLog.get().getId()).isEqualTo(savedMailLog.getId());
		assertThat(foundMailLog.get().getSender()).isEqualTo("sender@example.com");
	}

	@Test
	@DisplayName("delete a mailLog and verify it no longer exists")
	public void testDeleteMailLog() {
		// Given
		MailLog mailLog = MailLog.builder()
				.sender("sender@example.com")
				.recipient("recipient@example.com")
				.subject("delete test")
				.body("this email log will be deleted")
				.sentAt(LocalDateTime.now())
				.build();
		MailLog savedMailLog = mailLogRepository.save(mailLog);

		// When
		mailLogRepository.deleteById(savedMailLog.getId());
		Optional<MailLog> foundMailLog = mailLogRepository.findById(savedMailLog.getId());

		// Then
		assertThat(foundMailLog).isNotPresent();
	}

	@Test
	@DisplayName("update a mailLog and verify the updated data is stored correctly")
	public void testUpdateMailLog() {
		// Given
		MailLog mailLog = MailLog.builder()
				.sender("sender@example.com")
				.recipient("recipient@example.com")
				.subject("initial subject")
				.body("this is the initial body")
				.sentAt(LocalDateTime.now())
				.build();
		MailLog savedMailLog = mailLogRepository.save(mailLog);

		// When
		savedMailLog.setSubject("updated subject");
		savedMailLog.setBody("this is the updated body");
		MailLog updatedMailLog = mailLogRepository.save(savedMailLog);

		// Then
		assertThat(updatedMailLog.getId()).isEqualTo(savedMailLog.getId());
		assertThat(updatedMailLog.getSubject()).isEqualTo("updated subject");
		assertThat(updatedMailLog.getBody()).isEqualTo("this is the updated body");
	}
}

