package org.telegram.forcesubmultibot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class GetChannelLinks {

	private static final Logger log = LoggerFactory.getLogger(GetChannelLinks.class);
	private final RestTemplateBuilder restTemplateBuilder;

	public GetChannelLinks(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplateBuilder = restTemplateBuilder;
	}

	public String getChannelLinks(String channelName, String botToken) {
		String url = "https://api.telegram.org/bot" + botToken + "/exportChatInviteLink?chat_id=" + channelName;
		ResponseEntity<Map> response = restTemplateBuilder.build().getForEntity(url, Map.class);
		if (response.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(response.getBody().get("ok"))) {
			return (String) response.getBody().get("result");
		} else {
			log.error("Failed to get channel link: {}", response.getBody());
			return null;
		}
	}
}
