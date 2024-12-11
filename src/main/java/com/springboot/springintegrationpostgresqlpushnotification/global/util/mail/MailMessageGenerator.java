package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
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
		try {
			Map<String, Object> variables = variableExtractor.extract(templateName, dMap);
			Context genCtx = contextGenerator.convertMapToContext(variables);
			String genHtmlCtx = templateEngine.process(templateName, genCtx);
			MailMessage genMailMessage = new MailMessage(
					subject,
					genHtmlCtx,
					email
			);
			return genMailMessage;

		} catch (IOException e) {
			throw new MailSendException("템플릿 파일을 읽을 수 없습니다: " + e.getMessage(), e);
		} catch (TemplateInputException e) {
			return new MailMessage(subject, "기본 이메일 내용입니다. 템플릿 처리에 실패했습니다.", email);
		}
	}
}
