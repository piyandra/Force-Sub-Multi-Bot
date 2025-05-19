package org.telegram.forcesubmultibot.handler.command.handle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.ChannelType;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.entity.ForceSubChannel;
import org.telegram.forcesubmultibot.entity.Users;
import org.telegram.forcesubmultibot.handler.command.CommandProcessor;
import org.telegram.forcesubmultibot.service.ForceSubService;
import org.telegram.forcesubmultibot.service.GetChannelLinks;
import org.telegram.forcesubmultibot.service.UserService;
import org.telegram.forcesubmultibot.utils.message.SendingMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class AddForceSubChannel implements CommandProcessor {

    private static final Logger log = LoggerFactory.getLogger(AddForceSubChannel.class);
    private final SendingMessage sendingMessage;
    private final UserService userService;
    private final GetChannelLinks getChannelLinks;
    private final ForceSubService forceSubService;

    public AddForceSubChannel(SendingMessage sendingMessage, UserService userService,
							  GetChannelLinks getChannelLinks, ForceSubService forceSubService) {
        this.sendingMessage = sendingMessage;
        this.userService = userService;
        this.getChannelLinks = getChannelLinks;
        this.forceSubService = forceSubService;
    }

    @Override
    public String command() {
        return "/addfs";
    }

    @Override
    public CompletableFuture<Void> process(Configuration configuration, Update update) {
        return CompletableFuture.runAsync(() -> {
            // Get chat ID for sending messages
            Long chatId = update.getMessage().getChatId();
            String chatIdStr = chatId.toString();
            String botToken = configuration.getBotToken();
            
            // Check permissions
            if (!chatId.equals(configuration.getUserId().getUserId())) {
                sendingMessage.sendMessage(chatIdStr, "Anda tidak memiliki izin ini", null, botToken);
                return;
            }
            
            // Validate command format
            String[] commandParts = update.getMessage().getText().split(" ");
            if (commandParts.length < 2) {
                sendingMessage.sendMessage(chatIdStr, "Silahkan masukkan ID Channel", null, botToken);
                return;
            }
            
            // Get channel ID from command
            String channelId = commandParts[1];
            
            // Validate channel ID format
            if (!isValidChannelId(channelId)) {
                sendingMessage.sendMessage(chatIdStr, "ID Channel tidak valid", null, botToken);
                return;
            }
            
            try {
                // Get user entity
                Users user = userService.getUser(chatId).get();
                if (user == null) {
                    log.error("User not found for chat ID: {}", chatId);
                    sendingMessage.sendMessage(chatIdStr, "Akun pengguna tidak ditemukan", null, botToken);
                    return;
                }
                
                // Get channel links
                String links = getChannelLinks.getChannelLinks(channelId, botToken);
                if (links == null) {
                    sendingMessage.sendMessage(chatIdStr, "ID Channel tidak valid atau bot tidak memiliki akses", 
                                               null, botToken);
                    return;
                }
                if (forceSubService.countDatabaseChannels(user, ChannelType.FORCE_SUB) > 50) {
                    log.info("Force Sub Limit");
                    sendingMessage.sendMessage(chatIdStr, "Anda sudah memiliki 1 channel Force Sub database", null, botToken);
                    return;
                }
                log.info("Force Sub Channel ID: {}", channelId);
                ForceSubChannel channel = ForceSubChannel.builder()
                    .channelType(ChannelType.FORCE_SUB)
                    .id(Long.parseLong(channelId))
                    .chatId(user)
                    .channelLinks(links)
                        .channelId(Long.parseLong(update.getMessage().getText().replace("/addfs ", "").replace(" ", "")))
                    .build();

                log.info("Saving in Database");
                
                forceSubService.saveForceSubChannel(channel);

                sendingMessage.sendMessage(chatIdStr, 
                    "Berhasil menambahkan Channel ke Database: " + links, null, botToken);
                
            } catch (InterruptedException | ExecutionException e) {
                log.error("Error processing /adddb command", e);
                sendingMessage.sendMessage(chatIdStr, 
                    "Terjadi kesalahan saat memproses permintaan", null, botToken);
            }
        });
    }

    /**
     * Validates if the channel ID is in correct Telegram format
     * Channel IDs should start with -100 followed by 10 digits
     */
    public boolean isValidChannelId(String id) {
        return id != null && id.matches("-100\\d{10}");
    }
}