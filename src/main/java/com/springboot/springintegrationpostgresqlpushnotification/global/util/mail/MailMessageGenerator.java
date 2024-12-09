package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.Map;

@Component
public class MailMessageGenerator {

	private final VariableExtractor variableExtractor;
	private final ContextGenerator contextGenerator;
	private final SpringTemplateEngine templateEngine;

	public MailMessageGenerator(VariableExtractor variableExtractor, ContextGenerator contextGenerator, SpringTemplateEngine templateEngine) {
		this.variableExtractor = variableExtractor;
		this.contextGenerator = contextGenerator;
		this.templateEngine = templateEngine;
	}

	public MailMessage generateMailMessage(String subject, String templateName, Map<String, Object> dMap, String email) throws IOException {
		Map<String, Object> variables = variableExtractor.extract(templateName, dMap);
		Context genCtx = contextGenerator.convertMapToContext(variables);
		String genHtmlCtx = templateEngine.process(templateName, genCtx);
		MailMessage genMailMessage = new MailMessage(
			subject,
			genHtmlCtx,
			email
		);

		return genMailMessage;
	}
}
