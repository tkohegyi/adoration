package org.rockhill.adoration.web.json;

import org.rockhill.adoration.helper.JsonField;

public class CurrentUserInformationJson {
    @JsonField
    public boolean isLoggedIn;
    @JsonField
    public boolean isAuthorized;
    @JsonField
    public Long personId;
    @JsonField
    public Long socialId;
    @JsonField
    public String socialEmail;
    @JsonField
    public String loggedInUserName;
    @JsonField
    public String userName;
    @JsonField
    public String languageCode;
    @JsonField
    public boolean isRegisteredAdorator;
    @JsonField
    public boolean isPrivilegedAdorator;
    @JsonField
    public boolean isAdoratorAdmin;
    @JsonField
    public Integer coordinatorId;  // id of a coordinator or -1 otherwise
    @JsonField
    public boolean isDailyCoordinator;
    @JsonField
    public boolean isHourlyCoordinator;
    @JsonField
    public String socialServiceUsed;

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
        socialServiceUsed = "Undetermined";
    }

    public boolean isPrivilegedUser() {
        return isPrivilegedAdorator || isAdoratorAdmin;
    }
}
