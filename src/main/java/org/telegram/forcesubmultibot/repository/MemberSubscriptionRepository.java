package org.telegram.forcesubmultibot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.telegram.forcesubmultibot.entity.MemberSubscription;

import java.util.Optional;

@Repository
public interface MemberSubscriptionRepository extends JpaRepository<MemberSubscription, Long> {

	Optional<MemberSubscription> findFirstByUserIdAndChannelId(Long userId, Long channelId);

}
