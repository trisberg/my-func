package com.example.myfunc;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

@SpringBootApplication
public class MyFuncApplication {

	Logger log = LoggerFactory.getLogger(MyFuncApplication.class);

	@Bean
	public Function<Message<JsonNode>, String> hello() {
		return m -> {
			Map<String, String> headers = extractCloudEventsHeaders(m.getHeaders());
			log.info("HEADERS: " + headers);
			log.info("PAYLOAD: " + m.getPayload());
			return m.getPayload().toString();
		};
	}

	private Map<String, String> extractCloudEventsHeaders(Map<String, Object> messageHeaders) {
		Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
		for (String key : messageHeaders.keySet()) {
			if (key.toLowerCase().startsWith("ce-")) {
				headers.put(key, messageHeaders.get(key).toString());
			}
		}
		return headers;
	}

	public static void main(String[] args) {
		SpringApplication.run(MyFuncApplication.class, args);
	}

}
