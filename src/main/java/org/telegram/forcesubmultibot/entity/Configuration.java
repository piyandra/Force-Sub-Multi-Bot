package org.telegram.forcesubmultibot.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "configurations")
public class Configuration {

	@Id
	@OneToOne
	private Users userId;

	private String botToken;

	private Boolean protectMedia;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String welcomeMessageNotJoined;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String helpMessage;

	@Lob
	@Column(columnDefinition = "TEXT")
	private String welcomeMessageJoined;
}
