package org.telegram.forcesubmultibot.utils.message;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SendingMessage {
    private final Map<String, TelegramClient> clientCache = new ConcurrentHashMap<>();

    /**
     * Get or create client from cache based on bot token
     * @param botToken The Telegram bot token
     * @return A cached TelegramClient instance
     */
    private TelegramClient getClient(String botToken) {
        return clientCache.computeIfAbsent(botToken, OkHttpTelegramClient::new);
    }

    /**
     * Sends a message to a chat
     *
     * @param chatId               Chat ID to send message to
     * @param text                 Message text
     * @param inlineKeyboardMarkup Optional keyboard markup
     * @param botToken             Bot token to use
     */
    public void sendMessage(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup, String botToken) {
        CompletableFuture.supplyAsync(() -> {
            try {
                TelegramClient client = getClient(botToken);
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .replyMarkup(inlineKeyboardMarkup)
                        .text(text)
                        .parseMode("Markdown")
                        .build();
                log.info("Sending Message");
                return client.execute(message);
            } catch (Exception e) {
                logTelegramException(e, "sending message to " + chatId);
            }
            return null;
        }).thenAccept(result -> {
            // no-op, just to convert to CompletableFuture<Void>
        });
    }

    /**
     * Edits an existing message
     *
     * @param chatId    Chat ID where message is located
     * @param messageId Message ID to edit
     * @param newText   New message text
     * @param markup    Optional new keyboard markup
     * @param botToken  Bot token to use
     */
    public void editMessage(String chatId, Integer messageId, String newText, InlineKeyboardMarkup markup, String botToken) {
        CompletableFuture.supplyAsync(() -> {
            try {
                TelegramClient client = getClient(botToken);
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

    /**
     * Forwards a message from one chat to another
     * @param fromChatId Source chat ID
     * @param toChatId Destination chat ID
     * @param messageId Message ID to forward
     * @param botToken Bot token to use
     * @return CompletableFuture that completes when the operation is done
     */
    public CompletableFuture<Void> forwardMessage(String fromChatId, String toChatId, Integer messageId, String botToken) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                TelegramClient client = getClient(botToken);
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

    /**
     * Logs exceptions from Telegram API calls in a consistent format
     * @param e The exception that occurred
     * @param operation Description of the operation that failed
     */
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
    public void sendMessage(String chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup, int messageId, String botToken) {
        CompletableFuture.supplyAsync(() -> {
            try {
                TelegramClient client = getClient(botToken);
                SendMessage message = SendMessage.builder()
                        .chatId(chatId)
                        .replyMarkup(inlineKeyboardMarkup)
                        .text(text)
                        .replyToMessageId(messageId)
                        .parseMode("Markdown")
                        .build();
                log.info("Sending Message");
                return client.execute(message);
            } catch (Exception e) {
                logTelegramException(e, "sending message to " + chatId);
            }
            return null;
        }).thenAccept(result -> {
            // no-op, just to convert to CompletableFuture<Void>
        });
    }

    /**
     * Cleanup method to close all client connections when the service is destroyed
     */
    @PreDestroy
    public void cleanup() {
        log.info("Cleaning up Telegram clients...");
        clientCache.clear();
        log.info("All Telegram clients cleared");
    }
}