package org.telegram.forcesubmultibot.handler.command.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.entity.Message;
import org.telegram.forcesubmultibot.handler.command.CommandProcessor;
import org.telegram.forcesubmultibot.service.ForceSubService;
import org.telegram.forcesubmultibot.service.GetChatMembers;
import org.telegram.forcesubmultibot.service.MessageService;
import org.telegram.forcesubmultibot.utils.button.StartCommandButton;
import org.telegram.forcesubmultibot.utils.message.SendingMessage;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class StartHandle implements CommandProcessor {
	private static final Logger log = LoggerFactory.getLogger(StartHandle.class);
	private final SendingMessage sendingMessage;
	private final StartCommandButton startCommandButton;
	private final ForceSubService forceSubService;
	private final GetChatMembers getChatMembers;
	private final MessageService messageService;

	public StartHandle(SendingMessage sendingMessage, StartCommandButton startCommandButton, ForceSubService forceSubService, GetChatMembers getChatMembers, MessageService messageService) {
		this.sendingMessage = sendingMessage;
		this.startCommandButton = startCommandButton;
		this.forceSubService = forceSubService;
		this.getChatMembers = getChatMembers;
		this.messageService = messageService;
	}

	@Override
	public String command() {
		return "/start";
	}

	@Override
	@Async("handle")
	public CompletableFuture<Void> process(Configuration configuration, Update update) {
		return CompletableFuture.runAsync(() -> {
			if (update.getMessage().getText().equals("/start")) {
				log.info("Get Message");
				log.info("Bot Token : {}", configuration.getBotToken());
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(),
						"Hallo " + update.getMessage().getFrom().getFirstName() + ",\n" +
								"Ini adalah bot Master untuk membuat Force Sub Bot.\n" +
								"Kamu bisa menghubungi admin untuk mendapatkan akses ke bot ini.\n",
						startCommandButton.get(),
						configuration.getBotToken());
				return;
			}
			String[] data = update.getMessage().getText().split(" ");
			List<Long> allForceSubscribed = forceSubService.getAllForceSubscribed(data[1]);
			List<String> chatMembers = getChatMembers.getChatMembers(update.getMessage().getChatId().toString(), configuration.getBotToken(), allForceSubscribed);
			if (chatMembers == null) {
				List<Message> messageByUUID = messageService.getMessageByUUID(data[1]);
				messageByUUID.forEach(message -> sendingMessage.forwardMessage(message.getChatId().toString(), update.getMessage().getChatId().toString(), message.getMessageId(), configuration.getBotToken()));
				return;
			}
			InlineKeyboardMarkup markup = startCommandButton.joinButton(chatMembers, "https://t.me/" + getMe(configuration.getBotToken())+ "?start=" + data[1]);
			sendingMessage.sendMessage(update.getMessage().getChatId().toString(),
					"Hallo " + update.getMessage().getFrom().getFirstName() + ",\n" +
							"Kamu Belum Join Channel Dibawah\n" +
							"Join Dahulu buat mendapatkan Kontennya\n",
					markup,
					configuration.getBotToken());
			log.info("Get Message {}", update.getMessage().getText());
		});
	}

	public String getMe(String botToken) {
		TelegramClient telegramClient = new OkHttpTelegramClient(botToken);
		GetMe getMe = GetMe.builder()
				.build();
		try {
			User execute = telegramClient.execute(getMe);
			return execute.getUserName();
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}
}