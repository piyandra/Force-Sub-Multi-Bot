package org.telegram.forcesubmultibot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "force_sub_channel")
public class ForceSubChannel {

	@Id
	private Long id;

	@ManyToOne
	private Users chatId;
	private String botToken;
	@Enumerated(EnumType.STRING)
	private ChannelType channelType;
	private String channelLinks;
}
