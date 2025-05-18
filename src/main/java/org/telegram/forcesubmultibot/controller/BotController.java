package org.telegram.forcesubmultibot.controller;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.entity.Users;
import org.telegram.forcesubmultibot.repository.UserRepository;
import org.telegram.forcesubmultibot.service.CommandService;
import org.telegram.forcesubmultibot.service.ConfigurationService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RestController
public class BotController {
	private static final Logger log = LoggerFactory.getLogger(BotController.class);
	private final Set<String> tokenList = new HashSet<>();
	private final UserRepository userRepository;
	private final CommandService commandService;
	private final ConfigurationService configurationService;

	@Value("${bot.token}")
	private String botToken;

	@Value("${user.id}")
	private String ownerId;

	public BotController(UserRepository userRepository, CommandService commandService, ConfigurationService configurationService) {
		this.userRepository = userRepository;
		this.commandService = commandService;
		this.configurationService = configurationService;
	}

	@PostMapping("/webhook/{botToken}")
	@Async("webhookExecutor")
	public CompletableFuture<String> webhook(@PathVariable String botToken, @RequestBody Update update) {
    	if (tokenList.contains(botToken)) {
    		// Process the update as usual
    		handleUpdate(botToken, update);
		} else {
    		// Bot token not in our local cache, check if it exists in database
    		log.info("Token not found in cache, checking database: {}", botToken);
    
    		return configurationService.getConfigurationByToken(botToken)
        		.thenCompose(configuration -> {
            		if (configuration != null) {
                		// Token exists in database, add it to our local cache
                		log.info("Found token in database, adding to cache: {}", botToken);
                		tokenList.add(botToken);
                
                		// Now process the update
                		return handleUpdate(botToken, update);
            		} else {
                		log.warn("Bot token not registered: {}", botToken);
                		return CompletableFuture.completedFuture("Bot not registered");
            		}
				});
			}
		return null;
		}

		// Helper method to handle the update
		private CompletableFuture<String> handleUpdate(String botToken, Update update) {
    		Long chatId;
    		if (update.hasMessage()) {
        		chatId = update.getMessage().getChatId();
    		} else if (update.hasCallbackQuery()) {
        		chatId = update.getCallbackQuery().getMessage().getChatId();
    		} else {
        		chatId = null;
    		}

    		log.info("Webhook update: {}", update.getMessage() != null ?
					update.getMessage().getText() :
        		(update.getCallbackQuery() != null ? update.getCallbackQuery().getData() : "unknown update type"));
    		log.info("Chat ID: {}", chatId);
    
    		return configurationService.getConfigurationByToken(botToken)
        		.thenApply(configuration -> {
            		if (configuration == null) {
                		log.warn("No configuration found for bot token: {}", botToken);
                		return "Configuration not found";
            		}
            		log.info("Configuration Bot Token is {}", configuration.getBotToken());
            		commandService.handle(configuration, update);
            		return "OK";
        		});
		}

	@PostConstruct
	public void init() {
		long start = System.currentTimeMillis();
		log.info("Initializing BotController with bot token: {}", botToken);
		System.out.println();
		configurationService.saveConfiguration(Configuration.builder()
						.botToken(botToken)
						.userId(userRepository.save(Users.builder().userId(Long.parseLong(ownerId)).botToken(botToken).build()))
				.build());
		tokenList.add(botToken);
		List<Users> all = userRepository.findAll();
		for (Users users : all) {
			if (!users.getBotToken().equals(botToken)) {
				tokenList.add(users.getBotToken());
			}
		}
		log.info("BotController initialized with {} tokens in {} ms", tokenList.size(), System.currentTimeMillis() - start);

	}
}