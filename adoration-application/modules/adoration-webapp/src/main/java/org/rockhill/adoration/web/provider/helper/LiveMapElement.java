package org.rockhill.adoration.web.provider.helper;

import org.rockhill.adoration.web.json.CurrentUserInformationJson;

/**
 * Element of the LiveMap.
 */
public class LiveMapElement {
    private static final long TIMEOUT = 60000; //60 sec is the timeout

    private CurrentUserInformationJson currentUserInformationJson;
    private long deadline;

    /**
     * Creating a new LiveMap element.
     *
     * @param currentUserInformationJson is the actual user
     */
    public LiveMapElement(CurrentUserInformationJson currentUserInformationJson) {
        extend();
        this.currentUserInformationJson = currentUserInformationJson;
    }

    /**
     * Extend the validity of the map element with a predefined time period.
     */
    public void extend() {
        deadline = System.currentTimeMillis() + TIMEOUT;
    }

    public long getDeadline() {
        return deadline;
    }

    public CurrentUserInformationJson getCurrentUserInformationJson() {
        return currentUserInformationJson;
    }
}