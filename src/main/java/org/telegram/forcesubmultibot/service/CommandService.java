package org.telegram.forcesubmultibot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.handler.callback.CallbackProcessor;
import org.telegram.forcesubmultibot.handler.command.CommandProcessor;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class CommandService {

	private static final Logger log = LoggerFactory.getLogger(CommandService.class);
	private final Map<String, CommandProcessor> commandProcessors;
	private final Map<String, CallbackProcessor> callbackProcessors;

	public CommandService(List<CommandProcessor> commandProcessors, List<CallbackProcessor> callbackProcessors) {
		this.commandProcessors = commandProcessors.stream()
				.collect(Collectors.toMap(CommandProcessor::command, commandProcessor -> commandProcessor));
		this.callbackProcessors = callbackProcessors.stream()
				.collect(Collectors.toMap(CallbackProcessor::command, callbackProcessor -> callbackProcessor));

	}

	@Async
	public CompletableFuture<Void> handle(Configuration configuration, Update update) {
		log.info("Handling update: {}", update.getMessage() != null ? update.getMessage().getText() : update.getCallbackQuery().getData());
		if (update.hasMessage() && update.getMessage().hasText()) {
			String text = update.getMessage().getText().trim().split(" ")[0];
			CommandProcessor commandProcessor = commandProcessors.getOrDefault(text, null);
			if (commandProcessor == null) {
				log.info("Command not found: {}", text);
				return CompletableFuture.completedFuture(null);
			}
			return commandProcessor.process(configuration, update);
		} else if (update.hasCallbackQuery()) {
			String text = update.getCallbackQuery().getData().trim().split(" ")[0];
			CallbackProcessor callbackProcessor = callbackProcessors.getOrDefault(text, null);
			return callbackProcessor.process(configuration, update);
			}

		return CompletableFuture.completedFuture(null);
	}
}
