package org.telegram.forcesubmultibot.service;

import org.springframework.stereotype.Service;
import org.telegram.forcesubmultibot.entity.Message;
import org.telegram.forcesubmultibot.repository.MessageRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MessageService {

	private final MessageRepository messageRepository;

	public MessageService(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	public List<Message> getMessageByUUID(String uuid) {
		return messageRepository.findByUuid(uuid);
	}

	/**
	 * Saves a message to the database asynchronously and returns the UUID immediately
	 *
	 * @param message The message to save
	 * @return The generated UUID
	 */
	public String saveMessage(Message message) {
		String uuid = UUID.randomUUID().toString();
		message.setUuid(uuid);
		CompletableFuture.runAsync(() -> messageRepository.save(message));
		return uuid;
	}
}