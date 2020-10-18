package org.rockhill.adoration.web.provider.helper;

import org.rockhill.adoration.web.json.CurrentUserInformationJson;

public class LiveMapElement {
    private static long TIMEOUT = 60000; //60 sec is the timeout
    private CurrentUserInformationJson currentUserInformationJson;
    private long deadline;

    public LiveMapElement(CurrentUserInformationJson currentUserInformationJson) {
        extend();
        this.currentUserInformationJson = currentUserInformationJson;
    }

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