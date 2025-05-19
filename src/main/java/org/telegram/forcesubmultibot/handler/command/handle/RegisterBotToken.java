package org.telegram.forcesubmultibot.handler.command.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.entity.Users;
import org.telegram.forcesubmultibot.handler.command.CommandProcessor;
import org.telegram.forcesubmultibot.service.ConfigurationService;
import org.telegram.forcesubmultibot.service.SetUpWebhook;
import org.telegram.forcesubmultibot.service.UserService;
import org.telegram.forcesubmultibot.utils.message.SendingMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class RegisterBotToken implements CommandProcessor {


	private static final Logger log = LoggerFactory.getLogger(RegisterBotToken.class);
	private final SendingMessage sendingMessage;
	private final UserService userService;
	private final ConfigurationService configurationService;
	private final SetUpWebhook setUpWebhook;
	@Value("${user.id}")
	private String ownerId;

	public RegisterBotToken(SendingMessage sendingMessage, UserService userService, ConfigurationService configurationService, SetUpWebhook setUpWebhook) {
		this.sendingMessage = sendingMessage;
		this.userService = userService;
		this.configurationService = configurationService;
		this.setUpWebhook = setUpWebhook;
	}

	@Override
	public String command() {
		return "/add";
	}

	@Override
	public CompletableFuture<Void> process(Configuration configuration, Update update) {
		log.info("Get Message");
		return CompletableFuture.runAsync(() -> {
			if (!update.getMessage().getChatId().toString().equals(ownerId)) {
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), "Kamu bukan owner bot ini", null, configuration.getBotToken());
				return;
			}
			
			String[] data = update.getMessage().getText().split(" ");
			
			// Check if there are enough parameters
			if (data.length < 3) {
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), 
					"Format salah. Gunakan: /add <bot_token> <user_id>", null, configuration.getBotToken());
				return;
			}
			
			String botToken = data[1];
			String ownerIdStr = data[2];
			
			// Check if parameters are not empty
			if (botToken.isEmpty() || ownerIdStr.isEmpty()) {
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), 
					"Bot token dan user ID tidak boleh kosong", null, configuration.getBotToken());
				return;
			}

			long parsedOwnerId;
			try {
				parsedOwnerId = Long.parseLong(ownerIdStr);
			} catch (NumberFormatException e) {
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), 
					"User ID harus berupa angka", null, configuration.getBotToken());
				return;
			}
			
			// Validate bot token format (this is a simple check, adjust as needed)
			if (!botToken.contains(":")) {
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), 
					"Format bot token tidak valid", null, configuration.getBotToken());
				return;
			}
			

			try {
				Configuration existingConfig = configurationService.getConfiguration(parsedOwnerId).get();
				if (existingConfig != null &&
					existingConfig.getUserId() != null &&
					existingConfig.getUserId().getUserId().equals(parsedOwnerId)) {
					sendingMessage.sendMessage(update.getMessage().getChatId().toString(),
						"User ID sudah terdaftar", null, configuration.getBotToken());
					return;
				}
				Configuration newConfiguration = new Configuration();
				newConfiguration.setBotToken(botToken);
				try {
					newConfiguration.setUserId(userService.saveUser(Users.builder()
							.botToken(botToken)
							.userId(parsedOwnerId)
							.build()).get());
				} catch (InterruptedException | ExecutionException e) {
					log.error("Error saving user", e);
					sendingMessage.sendMessage(update.getMessage().getChatId().toString(),
							"Terjadi kesalahan saat menyimpan data", null, configuration.getBotToken());
					return;
				}

				configurationService.saveConfiguration(newConfiguration);
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), 
					"Bot token " + botToken + " berhasil didaftarkan", null, configuration.getBotToken());
				setUpWebhook.setWebhook(botToken);
				
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error checking existing configuration", e);
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), 
					"Terjadi kesalahan saat memeriksa konfigurasi", null, configuration.getBotToken());
			}
		});
	}
}