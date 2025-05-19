package org.telegram.forcesubmultibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesubmultibot.entity.Message;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, String> {
	List<Message> findByUuid(String uuid);
}
