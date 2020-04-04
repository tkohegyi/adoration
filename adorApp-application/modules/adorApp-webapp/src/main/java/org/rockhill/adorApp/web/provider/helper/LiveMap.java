package org.rockhill.adorApp.web.provider.helper;

import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LiveMap {
    private static final Logger logger = LoggerFactory.getLogger(LiveMap.class);
    private final Object o = new Object();
    private Map<String, LiveMapElement> liveMap = new ConcurrentHashMap<>();
    private static Timer liveMapCheckerTimer;

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
            if (liveMap.get(uuid) == null) {
                logger.info("Live Adorator joined - " + currentUserInformationJson.userName + " - " + uuid);
            }
            liveMap.putIfAbsent(uuid,new LiveMapElement(currentUserInformationJson));
            if (liveMapCheckerTimer == null) { //initiate timer
                liveMapCheckerTimer = new Timer(true);
                liveMapCheckerTimer.scheduleAtFixedRate(new LiveMapTimerTask(this),30000,30000);
            }
        }
        return uuid;
    }

    public void reNewLiveAdorator(String hashString) {
        synchronized (o) {
            if (liveMap.containsKey(hashString)) {
                LiveMapElement liveMapElement = liveMap.get(hashString);
                liveMapElement.extend();
                logger.info("Live Adorator extended - " + liveMapElement.getCurrentUserInformationJson().userName +" - " + hashString);
            } else {
                logger.warn("Unexpected incoming tick.");
            }
        }
    }

    /**
     * This is to clean up obsolete entries from the map.
     */
    public void timerTick() {
        logger.info("LiveMap Timer tick... online adorators: " + liveMap.size());
        if (!liveMap.isEmpty()) {
            long now = System.currentTimeMillis();
            synchronized (o) {
                String[] keySet = liveMap.keySet().toArray(new String[liveMap.size()]);
                for (int i = 0; i < keySet.length; i++) {
                    String entryKey = keySet[i];
                    LiveMapElement liveMapElement = liveMap.get(entryKey);
                    if (liveMapElement.getDeadline() < now) { //if expired
                        liveMap.remove(entryKey); //remove the entry
                        logger.info("Live Adorator left - " + liveMapElement.getCurrentUserInformationJson().userName +" - " + entryKey);
                    }
                }
            }
        }
    }
}
