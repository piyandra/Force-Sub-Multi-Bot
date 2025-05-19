package org.telegram.forcesubmultibot.utils.button;

import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.service.ForceSubService;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartCommandButton {

	public static final String HELP = "help";
	public static final String OWNER = "https://t.me/anggaran_apbn";
	private final ForceSubService forceSubService;

	public StartCommandButton(ForceSubService forceSubService) {
		this.forceSubService = forceSubService;
	}

	public InlineKeyboardMarkup get() {
		List<InlineKeyboardRow> rowList = new ArrayList<>();
		InlineKeyboardRow row = new InlineKeyboardRow();
		row.add(InlineKeyboardButton.builder()
						.text("HELP")
						.callbackData(HELP)
				.build());
		row.add(InlineKeyboardButton.builder()
						.text("BOT OWNER")
						.url(OWNER)
				.build());
		rowList.add(row);
		return InlineKeyboardMarkup.builder()
				.keyboard(rowList)
				.build();
	}
	public InlineKeyboardMarkup joinButton(List<String> channelIds, String url) {
		List<String> channelNames = new ArrayList<>();
		for (String channelId : channelIds) {
			channelNames.add(forceSubService.getForceSubByChannelId(Long.parseLong(channelId)));
		}
		List<InlineKeyboardRow> rows = new ArrayList<>();
		InlineKeyboardRow currentRow = new InlineKeyboardRow();

		for (int i = 0; i < channelIds.size(); i++) {
			InlineKeyboardButton button = InlineKeyboardButton.builder()
					.text("Join Channel")
					.url(channelNames.get(i))
					.build();
			currentRow.add(button);

			if (currentRow.size() == 2 || i == channelIds.size() - 1) {
				rows.add(currentRow);
				currentRow = new InlineKeyboardRow();
			}
		}
		InlineKeyboardRow urlButtonRow = new InlineKeyboardRow();
		InlineKeyboardButton urlButton = InlineKeyboardButton.builder()
				.text("Coba Lagi")
				.url(url)
				.build();
		urlButtonRow.add(urlButton);
		rows.add(urlButtonRow);

		return new InlineKeyboardMarkup(rows);
	}
}
