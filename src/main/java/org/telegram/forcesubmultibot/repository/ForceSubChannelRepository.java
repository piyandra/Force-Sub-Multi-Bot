package org.telegram.forcesubmultibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesubmultibot.entity.ChannelType;
import org.telegram.forcesubmultibot.entity.ForceSubChannel;
import org.telegram.forcesubmultibot.entity.Users;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForceSubChannelRepository extends JpaRepository<ForceSubChannel, Long> {
	void deleteByIdAndChannelLinks(Long id, String channelLinks);

	Long countByChatIdAndChannelTypeIs(Users chatId, ChannelType channelType);

	Optional<ForceSubChannel> findByChatIdAndChannelType(Users chatId, ChannelType channelType);

	Optional<ForceSubChannel> findByChannelLinks(String channelLinks);

	List<ForceSubChannel> findAllByChannelType(ChannelType channelType);

	boolean existsByChannelIdAndChannelType(Long channelId, ChannelType channelType);

	List<ForceSubChannel> findByChannelId(Long channelId);
}