package org.telegram.forcesubmultibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesubmultibot.entity.Users;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
	boolean findUsersByUserId(Long userId);

	Optional<Users> findUsersByBotToken(String botToken);

	Optional<Users> findByUserId(Long userId);
}
