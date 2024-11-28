package com.springboot.springintegrationpostgresqlpushnotification.global.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.store.JdbcChannelMessageStore;
import org.springframework.integration.jdbc.store.channel.PostgresChannelMessageStoreQueryProvider;

import javax.sql.DataSource;

@Configuration
public class SpringIntegrationConfiguration {
	private static final String METADATA_PREFIX = "_spring_integration_";

	@Bean
	JdbcChannelMessageStore jdbcChannelMessageStore(DataSource dataSource) {
		JdbcChannelMessageStore store = new JdbcChannelMessageStore(dataSource);
		store.setTablePrefix("_spring_integration_");
		store.setChannelMessageStoreQueryProvider(new PostgresChannelMessageStoreQueryProvider());
		return store;
	}
}
