package com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.repository;

import com.springboot.springintegrationpostgresqlpushnotification.domain.notification.data.entity.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, String> {
	List<Outbox> findByStatus(String status);

	@Modifying
	@Transactional
	@Query("UPDATE Outbox o SET o.status = :status WHERE o.id = :messageId")
	void updateStatus(@Param("messageId") String messageId, @Param("status") String status);

	@Modifying
	@Transactional
	@Query("DELETE FROM Outbox o WHERE o.status = 'FAILED' AND o.createdDttm < :timestamp")
	int deleteOldFailedMessages(@Param("timestamp") Timestamp timestamp);
}
