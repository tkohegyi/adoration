package org.rockhill.adorApp.web.json;

public class CurrentUserInformationJson {
    public boolean isLoggedIn;
    public Long id;
    public String loggedInUserName;
    public String userName;
    public boolean isRegisteredAdorator;
    public boolean isAdoratorLeader;
    public boolean isAdoratorAdmin;

    public CurrentUserInformationJson() {
        reset();
    }

    public void reset() {
        id = null;
        isLoggedIn = false;
        loggedInUserName = "Anonymous";
        userName = loggedInUserName;
        isRegisteredAdorator = false;
        isAdoratorLeader = false;
        isAdoratorAdmin = false;
    }
}
