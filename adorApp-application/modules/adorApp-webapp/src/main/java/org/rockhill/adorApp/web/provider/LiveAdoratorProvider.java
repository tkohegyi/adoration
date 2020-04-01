package org.rockhill.adorApp.web.provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LiveAdoratorProvider {

    private final Logger logger = LoggerFactory.getLogger(LiveAdoratorProvider.class);

    public String registerLiveAdorator() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public void incomingTick(String hashString) {
        if (hashString != null) {
            logger.info("Incoming tick from:" + hashString);
        }
    }
}
