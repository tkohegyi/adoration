package org.rockhill.adoration.web.controller.helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpSession;

public class ControllerBase {

    public static final String JSON_RESPONSE_UPDATE = "entityUpdate";
    public static final String JSON_RESPONSE_DELETE = "entityDelete";
    protected static final String UNAUTHORIZED_ACTION = "Unauthorized action.";
    protected static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public boolean isAdoratorAdmin(CurrentUserProvider currentUserProvider, HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        return currentUserInformationJson.isAdoratorAdmin;
    }

    public boolean isPrivilegedAdorator(CurrentUserProvider currentUserProvider, HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        return currentUserInformationJson.isPrivilegedAdorator;
    }

    public boolean isRegisteredAdorator(CurrentUserProvider currentUserProvider, HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        return currentUserInformationJson.isRegisteredAdorator;
    }

    public String getLanguageCode(CurrentUserProvider currentUserProvider, HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        return currentUserInformationJson.languageCode;
    }

    protected HttpHeaders setHeadersForJSON() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    protected String getJsonString(final String id, final Object object) {
        String json;
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(id, gson.toJsonTree(object));
        json = gson.toJson(jsonObject);
        return json;
    }

}
