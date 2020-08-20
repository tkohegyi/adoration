package org.rockhill.adorApp.web.json;

public class CurrentUserInformationJson {
    public boolean isLoggedIn;
    public boolean isAuthorized;
    public Long personId;
    public Long socialId;
    public String socialEmail;
    public String loggedInUserName;
    public String userName;
    public String languageCode;
    public boolean isRegisteredAdorator;
    public boolean isPrivilegedAdorator;
    public boolean isAdoratorAdmin;

    public CurrentUserInformationJson() {
        reset();
    }

    //  öéüőáóúűÖÉÜŐÁÓÚŰ
    //  \u00f6 \u00e9 \u00fc \u0151 \u00e1 \u00f3 \u00fa \u0171 \u00d6 \u00c9 \u00dc \u0150 \u00c1 \u00d3 \u00da \u0170
    public void reset() {
        personId = null;
        socialId = null;
        socialEmail = "";
        isLoggedIn = false;
        isAuthorized = false;
        loggedInUserName = "Anonymous";
        userName = loggedInUserName;
        languageCode = "hu"; //default language of the site
        isRegisteredAdorator = false;
        isPrivilegedAdorator = false;
        isAdoratorAdmin = false;
    }

    public boolean isPrivilegedUser() {
        return isPrivilegedAdorator || isAdoratorAdmin;
    }
}
