package com.springboot.springintegrationpostgresqlpushnotification.global.common.config;

import org.springframework.integration.jdbc.store.channel.PostgresChannelMessageStoreQueryProvider;

public class CustomPostgresChannelMessageStoreQueryProvider extends PostgresChannelMessageStoreQueryProvider {
	@Override
	public String getCreateMessageQuery() {
		return """
		    INSERT INTO %PREFIX%CHANNEL_MESSAGE (
			    MESSAGE_ID,
			    GROUP_KEY,
			    REGION,
			    CREATED_DATE,
			    MESSAGE_PRIORITY,
			    MESSAGE_BYTES)
			VALUES (?, ?, ?, TO_TIMESTAMP(?) AT TIME ZONE 'Asia/Seoul', ?, ?)
		""";
	}
}
