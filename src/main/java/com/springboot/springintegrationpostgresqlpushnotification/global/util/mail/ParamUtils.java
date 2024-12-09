package com.springboot.springintegrationpostgresqlpushnotification.global.util.mail;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class ParamUtils {
	public static Map<String, Object> addAndGetParams(Object... args) {
		Map<String, Object> paramMap = new HashMap<>();

		try {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			String methodName = stackTrace[2].getMethodName();
			String className = stackTrace[2].getClassName();

			Class<?> clazz = Class.forName(className);
			Method method = null;

			for (Method m : clazz.getDeclaredMethods()) {
				if (m.getName().equals(methodName) && m.getParameterCount() == args.length) {
					method = m;
					break;
				}
			}

			if (method == null) {
				throw new RuntimeException("해당 메서드 확인 불가: " + methodName);
			}

			Parameter[] parameters = method.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				paramMap.put(parameters[i].getName(), args[i]);
			}

		} catch (Exception e) {
			throw new RuntimeException("매개변수 처리 에러", e);
		}

		return paramMap;
	}
}
