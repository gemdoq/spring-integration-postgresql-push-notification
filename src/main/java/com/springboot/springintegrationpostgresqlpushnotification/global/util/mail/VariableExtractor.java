package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VariableExtractor {

	// method extract thymeleaf variables from html
	public Map<String, Object> extract(String templateFileName, Map<String, Object> dMap) throws IOException {

		// setting the html file path
		String templateFilePath = "src/main/resources/templates/" + templateFileName + ".html";

		// load a html file content to string
		String content = new String(Files.readAllBytes(Paths.get(templateFilePath)));

		// define pattern of thymeleaf
		Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");

		// define a matcher that matches the pattern in the template
		Matcher matcher = pattern.matcher(content);

		// create map to hold variable name
		Map<String, Object> variables = new HashMap<>();

		// extract variable name
		while (matcher.find()) {
			String variableName = matcher.group(1); // Extract variable name inside ${}
			if (dMap.containsKey(variableName)) {  // Check if dMap contains the key
				variables.put(variableName, dMap.get(variableName)); // Add key-value to variables map
			}
		}

		return variables;
	}
}
