package org.telegram.forcesubmultibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesubmultibot.entity.ForceSubChannel;

@Repository
public interface ForceSubChannelRepository extends JpaRepository<ForceSubChannel, Long> {
	void deleteByIdAndChannelLinks(Long id, String channelLinks);
}
