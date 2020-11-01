package org.rockhill.adoration.web.provider;

import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.helper.LiveMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Class to provide support/logging of live adorators.
 */
@Component
public class LiveAdoratorProvider {

    @Autowired
    private LiveMap liveMap;

    /**
     * Register a live adorator in the map.
     *
     * @param currentUserInformationJson is the actual user
     * @return with uuid assigned to this specific live adorator
     */
    public String registerLiveAdorator(CurrentUserInformationJson currentUserInformationJson) {
        String uuid;
        uuid = liveMap.registerLiveAdorator(currentUserInformationJson);
        return uuid;
    }

    /**
     * Realizes that the live adorator is still doing the adoration - so prolongs the uuid allocation.
     *
     * @param hashString is the live adorator identification - a uuid info
     */
    public void incomingTick(String hashString) {
        if (hashString != null) {
            liveMap.reNewLiveAdorator(hashString);
        }
    }
}
