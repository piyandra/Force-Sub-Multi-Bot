package org.telegram.forcesubmultibot.service;

import org.springframework.stereotype.Service;
import org.telegram.forcesubmultibot.entity.Message;
import org.telegram.forcesubmultibot.utils.message.SendingMessage;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.GetMe;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class GenerateMessageService {

	private final MessageService messageService;
	private final SendingMessage sendingMessage;

	public GenerateMessageService(MessageService messageService, SendingMessage sendingMessage) {
		this.messageService = messageService;
		this.sendingMessage = sendingMessage;
	}
	public String generateMessage(int messageId, Long chatId) {
		return messageService.saveMessage(Message.builder()
				.chatId(chatId)
				.messageId(messageId)
				.build());

	}
	public String getBotUsername(String botToken) {
		TelegramClient telegramClient = new OkHttpTelegramClient(botToken);
		GetMe me = new GetMe();
		try {
			User user = telegramClient.execute(me);
			return user.getUserName();
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	public void sendMessage(String botToken, int messageId, Long chatId) {
		String uuid = generateMessage(messageId, chatId);
		String botUsername = getBotUsername(botToken);
		String link = String.format("https://t.me/%s?start=%s", botUsername, uuid);

		String extractedUuid = link.split("\\?start=")[1];

		sendingMessage.sendMessage(
				chatId.toString(),
				String.format("""
            🔗 *Link Private Berhasil Dibuat*

            %s

            *Salin ID dibawah ini:*
            `%s`""", link, extractedUuid),
				null,
				messageId,
				botToken
		);
	}

}
