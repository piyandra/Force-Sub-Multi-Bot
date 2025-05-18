package org.telegram.forcesubmultibot.service;

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
    public CompletableFuture<Users> saveUser(Users users) {
        return CompletableFuture.supplyAsync(() -> {
            Users users1 = userRepository.findById(users.getUserId()).orElse(null);
            if (users1 != null) {
                users1.setUserId(users.getUserId());
                users1.setBotToken(users.getBotToken());
                return userRepository.save(users1);
            } else {
                return userRepository.save(users);
            }
        });
    }

    @Transactional(readOnly = true)
    public CompletableFuture<Users> getUser(Long userId) {
        return CompletableFuture.supplyAsync(() -> userRepository.findById(userId).orElse(null));
    }

    @Transactional(readOnly = true)
    public CompletableFuture<Users> getToken(String token) {
        return CompletableFuture.supplyAsync(() -> userRepository.findUsersByBotToken(token).orElse(null));
    }

    @Transactional(readOnly = true)
    public CompletableFuture<String> getBotToken(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            Users users = userRepository.findUsersByBotToken(userId).orElse(null);
            return users != null ? users.getBotToken() : null;
        });
    }

    @Transactional
    public CompletableFuture<Users> updateBotToken(Long userId, String botToken) {
        return CompletableFuture.supplyAsync(() -> 
            userRepository.findById(userId)
                .map(users -> {
                    users.setBotToken(botToken);
                    return userRepository.save(users);
                })
                .orElse(null)
        );
    }

    @Transactional
    public CompletableFuture<Void> deleteUser(Long userId) {
        return CompletableFuture.runAsync(() -> userRepository.deleteById(userId))
                .thenApply(v -> null);
    }

    @Transactional(readOnly = true)
    public CompletableFuture<Boolean> isUserExists(Long userId) {
        return CompletableFuture.supplyAsync(() -> userRepository.existsById(userId));
    }
}