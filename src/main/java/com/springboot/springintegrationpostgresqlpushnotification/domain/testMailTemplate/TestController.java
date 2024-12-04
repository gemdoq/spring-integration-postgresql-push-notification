package com.springboot.springintegrationpostgresqlpushnotification.domain.testMailTemplate;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
	private TestService testService;

	@GetMapping("/send-email")
	public String sendEmail(@RequestParam("to") String to, @RequestParam("name") String name, @RequestParam("code") String code) {
		try {
			testService.sendEmail(to, "this is title!", name, code);
			return "이메일 전송성공";
		} catch (MessagingException e) {
			return "이메일 전송 실패: " + e.getMessage();
		}
	}
}
