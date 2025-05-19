package org.telegram.forcesubmultibot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.forcesubmultibot.entity.MemberSubscription;
import org.telegram.forcesubmultibot.repository.MemberSubscriptionRepository;

import java.util.Optional;

@Service
public class MemberSubscriptionService {
	private static final Logger log = LoggerFactory.getLogger(MemberSubscriptionService.class);
	private final MemberSubscriptionRepository memberSubscriptionRepository;

	public MemberSubscriptionService(MemberSubscriptionRepository memberSubscriptionRepository) {
		this.memberSubscriptionRepository = memberSubscriptionRepository;
	}

	public MemberSubscription getMemberSubscription(Long channelId, Long userId) {
		return memberSubscriptionRepository.findFirstByUserIdAndChannelId(userId, channelId)
				.filter(subscription -> subscription.getExpiredAt() >= System.currentTimeMillis())
				.orElseGet(() -> {
					memberSubscriptionRepository.findFirstByUserIdAndChannelId(userId, channelId)
							.ifPresent(memberSubscriptionRepository::delete);
					return null;
				});
	}
	
	@Transactional
	public void saveMemberSubscription(MemberSubscription memberSubscription) {
		try {
			Optional<MemberSubscription> existingSubscription = 
					memberSubscriptionRepository.findFirstByUserIdAndChannelId(
							memberSubscription.getUserId(), 
							memberSubscription.getChannelId());
			
			if (existingSubscription.isPresent()) {
				// Update the existing subscription
				MemberSubscription subscription = existingSubscription.get();
				subscription.setExpiredAt(memberSubscription.getExpiredAt());
				memberSubscriptionRepository.save(subscription);
				log.info("Updated subscription for user {} to channel {}", 
						memberSubscription.getUserId(), memberSubscription.getChannelId());
			} else {
				// Create new subscription
				memberSubscriptionRepository.save(memberSubscription);
				log.info("Created new subscription for user {} to channel {}", 
						memberSubscription.getUserId(), memberSubscription.getChannelId());
			}
		} catch (OptimisticLockingFailureException e) {
			log.warn("Concurrent modification detected for user {} and channel {}, retrying...", 
					memberSubscription.getUserId(), memberSubscription.getChannelId());
			
			// Fetch fresh data and retry
			Optional<MemberSubscription> freshData = 
					memberSubscriptionRepository.findFirstByUserIdAndChannelId(
							memberSubscription.getUserId(), 
							memberSubscription.getChannelId());
			
			if (freshData.isPresent()) {
				MemberSubscription subscription = freshData.get();
				subscription.setExpiredAt(memberSubscription.getExpiredAt());
				memberSubscriptionRepository.save(subscription);
				log.info("Successfully updated subscription after retry");
			} else {
				memberSubscriptionRepository.save(memberSubscription);
				log.info("Successfully created subscription after retry");
			}
		}
	}
}