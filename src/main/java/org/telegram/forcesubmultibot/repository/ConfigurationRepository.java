package org.telegram.forcesubmultibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.forcesubmultibot.entity.Users;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

	Optional<Configuration> findByUserId(Users userId);
}
