package org.telegram.forcesubmultibot.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;

@RestController
public class BotController {

	@PostMapping("/webhook/{botToken}")
	@Async("webhookExecutor")
	public CompletableFuture<String> webhook(@PathVariable String botToken, @RequestBody Update update) {
		return CompletableFuture.completedFuture("OK");
	}
}
