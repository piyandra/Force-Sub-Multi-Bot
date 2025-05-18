package org.telegram.forcesubmultibot.handler.callback.handle;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.handler.callback.CallbackProcessor;
import org.telegram.forcesubmultibot.utils.button.HelpCommandButton;
import org.telegram.forcesubmultibot.utils.message.SendingMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;

@Component
public class HelpCallbackHandler implements CallbackProcessor {
	private final SendingMessage sendingMessage;
	private final HelpCommandButton helpCommandButton;

	public HelpCallbackHandler(SendingMessage sendingMessage1, HelpCommandButton helpCommandButton) {
		this.sendingMessage = sendingMessage1;
		this.helpCommandButton = helpCommandButton;
	}

	@Override
	public String command() {
		return "help";
	}

	@Override
	@Async
	public CompletableFuture<Void> process(Configuration configuration, Update update) {
		return CompletableFuture.runAsync(() -> sendingMessage.editMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
				update.getCallbackQuery().getMessage().getMessageId(),
				"Silahkan hubungi admin untuk mendapatkan bantuan lebih lanjut.",
				helpCommandButton.get(),configuration.getBotToken()));
	}
}
