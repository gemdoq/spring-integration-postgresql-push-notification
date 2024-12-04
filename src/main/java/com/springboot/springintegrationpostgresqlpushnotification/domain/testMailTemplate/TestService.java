package com.springboot.springintegrationpostgresqlpushnotification.domain.testMailTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
public class TestService {

	@Value("${MAIL_USERNAME}")
	private String MAIL_USERNAME;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private SpringTemplateEngine templateEngine;

	public void sendEmail(String to, String subject, String name, String code) throws MessagingException {
		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("code", code);
		String htmlContent = templateEngine.process("email-template", context);
		System.out.println("Template Engine에 의해 처리된 htmlContent: " + htmlContent);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(htmlContent, true);
		helper.setFrom(MAIL_USERNAME);

		mailSender.send(message);
	}
}
