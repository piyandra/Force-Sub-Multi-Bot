package org.telegram.forcesubmultibot.handler.command.handle;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.handler.command.CommandProcessor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;

@Component
public class StartHandle implements CommandProcessor {
	@Override
	public String command() {
		return "/start";
	}

	@Override
	@Async("handle")
	public CompletableFuture<Void> process(Configuration configuration, Update update) {
		return null;
	}
}
