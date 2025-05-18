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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
public class BotController {
	private static final Logger log = LoggerFactory.getLogger(BotController.class);
	private final List<String> tokenList = new ArrayList<>();
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
		return configurationService.getConfiguration(update.getMessage().getChatId())
				.thenApply(configuration -> {
					if (configuration == null) {
						log.warn("No configuration found for chat ID: {}", update.getMessage().getChatId());
						return "Configuration not found";
					}
					log.info("Configuration Bot Token is {}", configuration.getBotToken());
					commandService.handle(configuration, update);
					return "OK";
				});

	}
    return CompletableFuture.completedFuture("Not In Token");
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