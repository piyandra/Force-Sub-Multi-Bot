package org.telegram.forcesubmultibot.utils.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Slf4j
@Service
public class SendingMessage {

    public CompletableFuture<Void> sendMessage(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup, String botToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TelegramClient client = createClient(botToken);
                SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                        .replyMarkup(inlineKeyboardMarkup)
                    .text(text)
                    .build();
                log.info("Sending Message");
                return client.execute(message);  // join() returns Object, but should be fine
            } catch (Exception e) {
                logTelegramException(e, "sending message to " + chatId);
            }
            return null;
        }).thenAccept(result -> {
            // no-op, just to convert to CompletableFuture<Void>
        });
    }

    public CompletableFuture<Void> editMessage(String chatId, Integer messageId, String newText, InlineKeyboardMarkup markup, String botToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TelegramClient client = createClient(botToken);
                EditMessageText editMessage = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(messageId)
                        .replyMarkup(markup)
                    .text(newText)
                    .build();
                return client.execute(editMessage);
            } catch (Exception e) {
                logTelegramException(e, "editing message " + messageId + " in chat " + chatId);
                return null;
            }
        }).thenAccept(result -> {
            // no-op
        });
    }

    public CompletableFuture<Void> forwardMessage(String fromChatId, String toChatId, Integer messageId, String botToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TelegramClient client = createClient(botToken);
                CopyMessage copyMessage = CopyMessage.builder()
                    .fromChatId(fromChatId)
                    .chatId(toChatId)
                    .messageId(messageId)
                    .build();
                return client.execute(copyMessage);
            } catch (Exception e) {
                logTelegramException(e, "Copy message " + messageId + " from " + fromChatId + " to " + toChatId);
                return null;
            }
        }).thenAccept(result -> {
            // no-op
        });
    }

    private TelegramClient createClient(String botToken) {
        return new OkHttpTelegramClient(botToken);
    }

    private void logTelegramException(Throwable e, String operation) {
        Throwable cause = e instanceof CompletionException ? e.getCause() : e;
        if (cause instanceof TelegramApiException apiEx) {
			log.error("Telegram API error while {}: {}",
                operation, 
                apiEx.getMessage());
        } else {
            log.error("Error while {}: {}", operation, e.getMessage(), e);
        }
    }
}