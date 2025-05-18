package org.telegram.forcesubmultibot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class ForceSubMultibotApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForceSubMultibotApplication.class, args);
	}

}
