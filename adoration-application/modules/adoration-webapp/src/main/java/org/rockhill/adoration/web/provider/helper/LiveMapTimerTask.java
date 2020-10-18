package org.rockhill.adoration.web.provider.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class LiveMapTimerTask extends TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(LiveMapTimerTask.class);
    private final LiveMap liveMap;

    public LiveMapTimerTask(LiveMap liveMap) {
        this.liveMap = liveMap;
    }

    @Override
    public void run() {
        liveMap.timerTick();
    }
}
