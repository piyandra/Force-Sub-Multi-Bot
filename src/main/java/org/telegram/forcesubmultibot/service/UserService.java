package org.telegram.forcesubmultibot.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesubmultibot.entity.Users;
import org.telegram.forcesubmultibot.repository.UserRepository;

import java.util.concurrent.CompletableFuture;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	@Async("service")
	public CompletableFuture<Void> saveUser(Users users) {
		CompletableFuture.runAsync(() -> userRepository.save(users));
		return CompletableFuture.completedFuture(null);
	}

	@Transactional
	@Async("service")
	public CompletableFuture<Users> getUser(Long userId) {
		return CompletableFuture.completedFuture(userRepository.findById(userId).orElse(null));
	}
	@Transactional
	@Async("service")
	public CompletableFuture<String> getBotToken(Long userId) {
    return CompletableFuture.supplyAsync(() -> {
        Users users = userRepository.findById(userId).orElse(null);
        return users != null ? users.getBotToken() : null;
    });
}

	@Transactional
	@Async("service")
	public CompletableFuture<Void> updateBotToken(Long userId, String botToken) {
		CompletableFuture.runAsync(() -> userRepository.findById(userId).ifPresent(data -> userRepository.findById(userId).map(users -> {
			users.setBotToken(botToken);
			return userRepository.save(users);
		})));
		return CompletableFuture.completedFuture(null);
	}
	@Transactional
	@Async("service")
	public CompletableFuture<Void> deleteUser(Long userId) {
		CompletableFuture.runAsync(() -> userRepository.deleteById(userId));
		return CompletableFuture.completedFuture(null);
	}
	@Transactional
	@Async("service")
	public CompletableFuture<Boolean> isUserExists(Long userId) {
		return CompletableFuture.supplyAsync(() -> userRepository.existsById(userId));
	}
}