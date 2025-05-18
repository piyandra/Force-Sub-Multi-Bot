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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Users chatId;
	@Enumerated(EnumType.STRING)
	private ChannelType channelType;
	private String channelLinks;
}
