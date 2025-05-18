package org.telegram.forcesubmultibot.handler.command.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.handler.command.CommandProcessor;
import org.telegram.forcesubmultibot.utils.message.SendingMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;

@Component
public class StartHandle implements CommandProcessor {
	private static final Logger log = LoggerFactory.getLogger(StartHandle.class);
	private final SendingMessage sendingMessage;

	public StartHandle(SendingMessage sendingMessage) {
		this.sendingMessage = sendingMessage;
	}

	@Override
	public String command() {
		return "/start";
	}

	@Override
	@Async("handle")
	public CompletableFuture<Void> process(Configuration configuration, Update update) {
		return CompletableFuture.runAsync(() -> {
			log.info("Get Message");
			log.info("Bot Token : {}", configuration.getBotToken());
			sendingMessage.sendMessage(update.getMessage().getChatId().toString(), "Hello Dunia", configuration.getBotToken());
		});
	}
}
