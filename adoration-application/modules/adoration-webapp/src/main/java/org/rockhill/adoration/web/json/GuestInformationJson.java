package org.rockhill.adoration.web.json;

import java.util.List;

public class GuestInformationJson {
    public String error; // filled only in case of error
    public String socialServiceUsed;
    public Boolean isGoogle;
    public String nameGoogle;
    public String emailGoogle;
    public Boolean isFacebook;
    public String nameFacebook;
    public String emailFacebook;
    public String status;   //status of the adorator
    public String id;   //id of the social record
    public List<CoordinatorJson> leadership; //main coordinators
}
