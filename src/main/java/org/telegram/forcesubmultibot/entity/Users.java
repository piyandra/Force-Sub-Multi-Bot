package org.telegram.forcesubmultibot.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class Users {

	@Id
	private Long userId;

	private String botToken;

	@OneToMany(mappedBy = "chatId")
	private List<ForceSubChannel> forceSubChannels;

	@OneToMany
	private List<Message> messages;
}
