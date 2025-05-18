package org.telegram.forcesubmultibot.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.repository.ConfigurationRepository;

import java.util.concurrent.CompletableFuture;

/**
 * Service for managing application configurations.
 * All methods run asynchronously using the "service" executor.
 */
@Service
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;

    public ConfigurationService(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
    }

    /**
     * Saves a configuration asynchronously.
     *
     * @param configuration the configuration to save
     * @return CompletableFuture with void result
     */
    @Transactional
    @Async("service")
    public CompletableFuture<Void> saveConfiguration(Configuration configuration) {
        configurationRepository.save(configuration);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Retrieves a configuration by chat ID asynchronously.
     *
     * @param chatId the chat ID
     * @return CompletableFuture with the configuration or null if not found
     */
    @Transactional(readOnly = true)
    @Async("service")
    public CompletableFuture<Configuration> getConfiguration(Long chatId) {
        return CompletableFuture.completedFuture(configurationRepository.findById(chatId).orElse(null));
    }

    /**
     * Sets the help message for a chat asynchronously.
     *
     * @param chatId the chat ID
     * @param helpMessage the help message to set
     * @return CompletableFuture with void result
     */
    @Transactional
    @Async("service")
    public CompletableFuture<Void> setHelpMessage(Long chatId, String helpMessage) {
        CompletableFuture.runAsync(() -> configurationRepository.findById(chatId).map(data -> {
			data.setHelpMessage(helpMessage);
			return configurationRepository.save(data);
		}));
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Sets the welcome message for users who haven't joined asynchronously.
     *
     * @param chatId the chat ID
     * @param welcomeMessageNotJoined the welcome message to set
     * @return CompletableFuture with void result
     */
    @Transactional
    @Async("service")
    public CompletableFuture<Void> setWelcomeMessageNotJoined(Long chatId, String welcomeMessageNotJoined) {
        CompletableFuture.runAsync(() -> configurationRepository.findById(chatId).ifPresent(data -> {
			data.setWelcomeMessageNotJoined(welcomeMessageNotJoined);
			configurationRepository.save(data);
}));
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Sets the welcome message for users who have joined asynchronously.
     *
     * @param chatId the chat ID
     * @param welcomeMessageJoined the welcome message to set
     * @return CompletableFuture with void result
     */
    @Transactional
    @Async("service")
    public CompletableFuture<Void> setWelcomeMessageJoined(Long chatId, String welcomeMessageJoined) {
        configurationRepository.findById(chatId).ifPresent(data -> {
            data.setWelcomeMessageJoined(welcomeMessageJoined);
            configurationRepository.save(data);
        });
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Sets the media protection status asynchronously.
     *
     * @param chatId the chat ID
     * @param protectMedia whether to protect media
     * @return CompletableFuture with void result
     */
    @Transactional
    @Async("service")
    public CompletableFuture<Void> setProtectMedia(Long chatId, Boolean protectMedia) {
        CompletableFuture.runAsync(() -> configurationRepository.findById(chatId).ifPresent(data -> {
			data.setProtectMedia(protectMedia);
			configurationRepository.save(data);
}));
        return CompletableFuture.completedFuture(null);
    }
}