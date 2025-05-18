package org.telegram.forcesubmultibot.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesubmultibot.entity.ChannelType;
import org.telegram.forcesubmultibot.entity.ForceSubChannel;
import org.telegram.forcesubmultibot.entity.Users;
import org.telegram.forcesubmultibot.repository.ForceSubChannelRepository;
import org.telegram.forcesubmultibot.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

@Service
public class ForceSubService {

	private final ForceSubChannelRepository forceSubChannelRepository;
	private final UserRepository userRepository;

	public ForceSubService(ForceSubChannelRepository forceSubChannelRepository, UserRepository userRepository) {
		this.forceSubChannelRepository = forceSubChannelRepository;
		this.userRepository = userRepository;
	}

	@Async("service")
	@Transactional
	public CompletableFuture<Void> setForceSubChannel(String channelId, Users users, ChannelType channelType) {
		CompletableFuture.runAsync(() -> userRepository.findById(users.getUserId()).ifPresent(data -> forceSubChannelRepository.save(ForceSubChannel.builder()
				.chatId(data)
				.channelLinks(channelId)
				.channelType(channelType)
				.build())));
		return CompletableFuture.completedFuture(null);
	}
	@Async("service")
	@Transactional
	public CompletableFuture<Void> deleteForceSubChannel(Users users, String channel) {
		CompletableFuture.runAsync(() -> forceSubChannelRepository.deleteByIdAndChannelLinks(users.getUserId(), channel));
		return CompletableFuture.completedFuture(null);
	}
	@Async("service")
	@Transactional
	public CompletableFuture<Void> saveForceSubChannel(ForceSubChannel forceSubChannel) {
		CompletableFuture.runAsync(() -> forceSubChannelRepository.save(forceSubChannel));
		return CompletableFuture.completedFuture(null);
	}
}
