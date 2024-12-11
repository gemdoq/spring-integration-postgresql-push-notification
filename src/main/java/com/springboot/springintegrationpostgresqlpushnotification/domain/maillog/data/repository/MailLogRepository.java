package com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.repository;

import com.springboot.springintegrationpostgresqlpushnotification.domain.maillog.data.entity.MailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailLogRepository extends JpaRepository<MailLog, Long> {
}
