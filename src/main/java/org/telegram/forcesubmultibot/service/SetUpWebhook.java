package org.telegram.forcesubmultibot.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SetUpWebhook {

	private final NgrokService ngrokService;

	@Value("${bot.token}")
	private String botToken;

	public SetUpWebhook(NgrokService ngrokService) {
		this.ngrokService = ngrokService;
	}

	@PostConstruct
	public void init() {
		ngrokService.SetWebhook(botToken);
	}

	public void setWebhook(String botToken) {
		ngrokService.SetWebhook(botToken);
	}
}
