package com.example.myfunc;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;

import com.example.HelloWorld;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;

@SpringBootApplication
public class MyFuncApplication {

	Logger log = LoggerFactory.getLogger(MyFuncApplication.class);

	ObjectMapper objectMapper = new ObjectMapper();

	@Bean
	public Function<Message<JsonNode>, String> hello() {
		return m -> {
			Map<String, String> headers = extractCloudEventsHeaders(m.getHeaders());
			log.info("HEADERS: " + headers);
			log.info("PAYLOAD: " + m.getPayload());
			HelloWorld pojo = convertPayload(m.getPayload());
			if (pojo != null) {
				return pojo.toString();
			} else {
				return "Unable to parse: " + m.getPayload();
			}
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

	private HelloWorld convertPayload(JsonNode payload) {
		try {
			return objectMapper.treeToValue(payload, HelloWorld.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			return null;
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(MyFuncApplication.class, args);
	}

}
