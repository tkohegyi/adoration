package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.web.provider.helper.LiveMap;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class LiveAdoratorProvider {

    private final Logger logger = LoggerFactory.getLogger(LiveAdoratorProvider.class);

    @Autowired
    LiveMap liveMap;

    public String registerLiveAdorator(CurrentUserInformationJson currentUserInformationJson) {
        String uuid = liveMap.registerLiveAdorator(currentUserInformationJson);
        return uuid;
    }

    public void incomingTick(String hashString) {
        if (hashString != null) {
            liveMap.reNewLiveAdorator(hashString);
        }
    }
}
