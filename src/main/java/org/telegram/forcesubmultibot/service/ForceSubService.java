package org.telegram.forcesubmultibot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesubmultibot.entity.ChannelType;
import org.telegram.forcesubmultibot.entity.ForceSubChannel;
import org.telegram.forcesubmultibot.entity.Users;
import org.telegram.forcesubmultibot.repository.ForceSubChannelRepository;
import org.telegram.forcesubmultibot.repository.MessageRepository;
import org.telegram.forcesubmultibot.repository.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ForceSubService {

	private static final Logger log = LoggerFactory.getLogger(ForceSubService.class);
	private final ForceSubChannelRepository forceSubChannelRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;

	public ForceSubService(ForceSubChannelRepository forceSubChannelRepository, UserRepository userRepository, MessageRepository messageRepository) {
		this.forceSubChannelRepository = forceSubChannelRepository;
		this.userRepository = userRepository;
		this.messageRepository = messageRepository;
	}

	@Transactional
	public CompletableFuture<Void> setForceSubChannel(String channelId, Users users, ChannelType channelType) {
		return CompletableFuture.runAsync(() -> {
			Optional<Users> userOpt = userRepository.findById(users.getUserId());
			if (userOpt.isPresent()) {
				Users user = userOpt.get();
				Optional<ForceSubChannel> existingChannel = forceSubChannelRepository.findByChannelLinks(channelId);
				if (existingChannel.isPresent()) {
					// Update existing record instead of creating a duplicate
					ForceSubChannel channel = existingChannel.get();
					channel.setChatId(user);
					channel.setChannelType(channelType);
					forceSubChannelRepository.save(channel);
					log.info("Updated existing force sub channel for user: {} and channel: {}", user.getUserId(), channelId);
				} else {
					// Save new record
					forceSubChannelRepository.save(ForceSubChannel.builder()
							.chatId(user)
							.channelLinks(channelId)
							.channelType(channelType)
							.build());
					log.info("Created new force sub channel for user: {} and channel: {}", user.getUserId(), channelId);
				}
			} else {
				log.warn("User with ID {} not found", users.getUserId());
			}
		}).exceptionally(ex -> {
			log.error("Error setting force sub channel: {}", ex.getMessage(), ex);
			return null;
		});
	}

	@Transactional
	public CompletableFuture<Void> deleteForceSubChannel(Users users, String channel) {
		return CompletableFuture.runAsync(() -> {
			try {
				forceSubChannelRepository.deleteByIdAndChannelLinks(users.getUserId(), channel);
				log.info("Deleted force sub channel for user: {} and channel: {}", users.getUserId(), channel);
			} catch (Exception e) {
				log.error("Error deleting force sub channel: {}", e.getMessage(), e);
			}
		});
	}

	@Transactional
	public CompletableFuture<Void> saveForceSubChannel(ForceSubChannel forceSubChannel) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				// Check if a record with the same unique constraints already exists
				Optional<ForceSubChannel> existingChannel = forceSubChannelRepository.findByChannelLinks(forceSubChannel.getChannelLinks());
				if (existingChannel.isPresent()) {
					// Update existing record instead of creating a duplicate
					ForceSubChannel channel = existingChannel.get();
					channel.setChatId(forceSubChannel.getChatId());
					channel.setChannelType(forceSubChannel.getChannelType());
					forceSubChannelRepository.save(channel);
					log.info("Updated existing force sub channel: {}", channel.getChannelLinks());
				} else {
					// Save new record
					forceSubChannelRepository.save(forceSubChannel);
					log.info("Saved new force sub channel: {}", forceSubChannel.getChannelLinks());
				}
			} catch (Exception e) {
				log.error("Error saving force sub channel: {}", e.getMessage(), e);
			}
			return null;
		});
	}

	@Transactional(readOnly = true)
	public Long countDatabaseChannels(Users users, ChannelType channelType) {
		Long l = forceSubChannelRepository.countByChatIdAndChannelTypeIs(users, channelType);
		log.info("Count of channels in database: {}", l);
		return l;
	}

	@Transactional(readOnly = true)
	public Map<String, String> getForceSubChannel(Users users, ChannelType channelType) {
		return forceSubChannelRepository.findByChatIdAndChannelType(users, channelType).stream()
				.collect(Collectors.toMap(ForceSubChannel::getChannelLinks, channel -> channel.getId().toString()));
	}

	@Transactional
	public void deleteForceSub(String channelLinks) {
		CompletableFuture.runAsync(() -> {
			try {
				Optional<ForceSubChannel> channels = forceSubChannelRepository.findByChannelLinks(channelLinks);
				if (channels.isPresent()) {
					forceSubChannelRepository.delete(channels.get());
					log.info("Deleted force sub channel: {}", channelLinks);
				} else {
					log.warn("No force sub channel found with link: {}", channelLinks);
				}
			} catch (Exception e) {
				log.error("Error deleting force sub channel: {}", e.getMessage(), e);
			}
		});
	}

	@Transactional(readOnly = true)
	public List<Long> getAllDatabase() {
		log.info("Getting all database channels");
		return forceSubChannelRepository.findAllByChannelType(ChannelType.DATABASE).stream()
				.map(ForceSubChannel::getChannelId)
				.toList();
	}

	@Transactional(readOnly = true)
	public boolean isInDatabase(Long chatId) {
		return forceSubChannelRepository.existsByChannelIdAndChannelType(chatId, ChannelType.DATABASE);
	}

	public List<Long> getAllForceSubscribed(String uuid) {
		log.info("Getting all force subscribed channels");
		return forceSubChannelRepository.findByChannelId(messageRepository.findByUuid(uuid)
						.getFirst()
						.getChatId())
				.stream()
				.map(ForceSubChannel::getChannelId)
				.toList();

	}
	public String getForceSubByChannelId(Long channelId) {
		return forceSubChannelRepository.findByChannelId(channelId).getFirst().getChannelLinks();
	}
}