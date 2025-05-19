package org.telegram.forcesubmultibot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.MemberSubscription;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class GetChatMembers {

    private static final Logger log = LoggerFactory.getLogger(GetChatMembers.class);
    private final ForceSubService forceSubService;
    private final MemberSubscriptionService memberSubscriptionService;

    public GetChatMembers(ForceSubService forceSubService, MemberSubscriptionService memberSubscriptionService) {
        this.forceSubService = forceSubService;
        this.memberSubscriptionService = memberSubscriptionService;
    }
    
    public List<String> getChatMembers(String chatId, String botToken, List<Long> channelLinks) {
        log.info("Get Chat Members");
        TelegramClient telegramClient = new OkHttpTelegramClient(botToken);
        List<String> notJoined = new ArrayList<>();
        
        if (channelLinks == null || channelLinks.isEmpty()) {
            log.warn("No channel links provided");
            return notJoined;
        }
        
        try {
            for (Long channelLink : channelLinks) {
                log.info("Checking Channel {}", channelLink);
                
                try {
                    // Check if member is already in the database
                    MemberSubscription memberSubscription = memberSubscriptionService.getMemberSubscription(
                        channelLink, Long.parseLong(chatId));
                    
                    if (memberSubscription != null) {
                        log.info("Member Already Joined Channel {}", channelLink);
                        continue;
                    }
                    
                    log.info("Not Joined By Database, Finding By Telegram");
                    GetChatMember getChatMember = GetChatMember.builder()
                        .chatId(channelLink.toString())
                        .userId(Long.parseLong(chatId))
                        .build();
                    
                    ChatMember execute = telegramClient.execute(getChatMember);
                    if (execute.getStatus().equals("creator") || 
                        execute.getStatus().equals("administrator") || 
                        execute.getStatus().equals("member")) {
                        
                        log.info("Member Joined, Saving In Database {}", channelLink);
                        
                        // Save subscription
                        memberSubscriptionService.saveMemberSubscription(MemberSubscription.builder()
                            .channelId(channelLink)
                            .expiredAt(System.currentTimeMillis() + 1000L * 60 * 10)
                            .userId(Long.parseLong(chatId))
                            .build());
                    } else {
                        log.info("Member Not Joined Channel {}", channelLink);
                        
                        // Get the channel name for returned list
                        String channelName = forceSubService.getForceSubByChannelId(channelLink);
						notJoined.add(Objects.requireNonNullElseGet(channelName, channelLink::toString));
                    }
                } catch (Exception e) {
                    log.error("Error checking channel membership for channel {} and user {}: {}", 
                        channelLink, chatId, e.getMessage());
                    
                    // Add to not joined list when an error occurs
                    String channelName = null;
                    try {
                        channelName = forceSubService.getForceSubByChannelId(channelLink);
                    } catch (Exception ex) {
                        log.error("Could not get channel name: {}", ex.getMessage());
                    }
                    
                    notJoined.add(channelName != null ? channelName : channelLink.toString());
                }
            }
            
            return notJoined.isEmpty() ? null : notJoined;
            
        } catch (Exception e) {
            log.error("Error in getChatMembers: {}", e.getMessage(), e);
            return null;
        }
    }
}