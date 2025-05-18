package org.telegram.forcesubmultibot.handler.callback;

import org.springframework.stereotype.Component;
import org.telegram.forcesubmultibot.entity.Configuration;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.concurrent.CompletableFuture;

@Component
public interface CallbackProcessor {

	String command();
	CompletableFuture<Void> process(Configuration configuration, Update update);
}
