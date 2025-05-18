package org.telegram.forcesubmultibot.utils.button;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class HelpCommandButton {

	public InlineKeyboardMarkup get() {
		List<InlineKeyboardRow> rowList = new ArrayList<>();
		InlineKeyboardRow row = new InlineKeyboardRow();
		row.add(InlineKeyboardButton.builder()
				.text("BACK")
				.callbackData("BACK")
				.build());
		rowList.add(row);
		return InlineKeyboardMarkup.builder()
				.keyboard(rowList)
				.build();
	}
}
