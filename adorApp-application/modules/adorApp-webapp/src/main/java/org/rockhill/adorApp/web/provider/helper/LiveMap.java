package org.rockhill.adorApp.web.provider.helper;

import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveMap {
    private static final Logger logger = LoggerFactory.getLogger(LiveMap.class);
    private final Object o = new Object();
    private Map<String, LiveMapElement> liveMap = new ConcurrentHashMap<>();

    /**
     * Method that generates the list of the live users in JSON format.
     *
     * @return with the response body
     */
    public String getLiveMapAsResponse() {
        StringBuilder response = new StringBuilder("{\n  \"liveMap\": [\n");
        if (!liveMap.isEmpty()) {
            synchronized (o) {
                String[] keySet = liveMap.keySet().toArray(new String[liveMap.size()]);
                for (int i = 0; i < keySet.length; i++) {
                    String entryKey = keySet[i];
                    response.append("    { \"").append(entryKey).append("\": \"")
                            .append(liveMap.get(entryKey).getClass().getCanonicalName()).append("\" }");
                    if (i < keySet.length - 1) {
                        response.append(",");
                    }
                    response.append("\n");
                }
            }
        }
        response.append("  ]\n}\n");
        return response.toString();
    }

    public String registerLiveAdorator(CurrentUserInformationJson currentUserInformationJson) {
        String uuid = UUID.randomUUID().toString();
        synchronized (o) {
            liveMap.putIfAbsent(uuid,new LiveMapElement(currentUserInformationJson));
        }
        return uuid;
    }

    public void reNewLiveAdorator(String hashString) {
        synchronized (o) {
            if (liveMap.containsKey(hashString)) {
                liveMap.get(hashString).extend();
            } else {
                logger.warn("Unexpected incoming tick.");
            }
        }
    }
}
