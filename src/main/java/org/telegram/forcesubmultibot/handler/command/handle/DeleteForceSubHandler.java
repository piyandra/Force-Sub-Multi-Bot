package org.telegram.forcesubmultibot.handler.command.handle;

import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.ChannelType;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.handler.command.CommandProcessor;
import org.telegram.forcesubmultibot.service.ForceSubService;
import org.telegram.forcesubmultibot.service.UserService;
import org.telegram.forcesubmultibot.utils.message.SendingMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class DeleteForceSubHandler implements CommandProcessor {
	private final SendingMessage sendingMessage;
	private final ForceSubService forceSubService;
	private final UserService userService;

	public DeleteForceSubHandler(SendingMessage sendingMessage, ForceSubService forceSubService, UserService userService) {
		this.sendingMessage = sendingMessage;
		this.forceSubService = forceSubService;
		this.userService = userService;
	}

	@Override
	public String command() {
		return "/deleteforcesub";
	}

	@Override
	@Async
	public CompletableFuture<Void> process(Configuration configuration, Update update) {
		return CompletableFuture.runAsync(() -> {
			if (!update.getMessage().getChatId().equals(configuration.getUserId().getUserId())) {
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), "Anda tidak memiliki akses untuk menggunakan perintah ini", null,configuration.getBotToken());
				return;
			}
			String[] data = update.getMessage().getText().split(" ");
			if (data.length < 2) {
				sendingMessage.sendMessage(update.getMessage().getChatId().toString(), "Silahkan masukkan Link Force Sub yang ingin dihapus " + mapAllForceSubToString(update.getMessage().getChatId()), null,configuration.getBotToken());
				return;
			}
			String forceSubId = data[1];
			forceSubService.deleteForceSub(forceSubId);
			sendingMessage.sendMessage(update.getMessage().getChatId().toString(), "Data ForceSub Berhasil Di Delete", null,configuration.getBotToken());

		});
	}
	@SneakyThrows
	private String mapAllForceSubToString(Long chatId) {
		Map<String, String> forceSubChannel = forceSubService.getForceSubChannel(userService.getUser(chatId).get(), ChannelType.FORCE_SUB);
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Berikut adalah daftar Force Sub yang terdaftar di bot ini:\n");
		forceSubChannel.forEach((key, value) -> stringBuilder.append(key).append(" -> ").append(value).append("\n"));
		return stringBuilder.toString();
	}


}
