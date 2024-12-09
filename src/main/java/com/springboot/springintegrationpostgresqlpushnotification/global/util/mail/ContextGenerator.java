package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
public class ContextGenerator {

	public Context convertMapToContext(Map<String, Object> variables) {
		Context context = new Context();
		context.setVariables(variables);
		return context;
	}
}