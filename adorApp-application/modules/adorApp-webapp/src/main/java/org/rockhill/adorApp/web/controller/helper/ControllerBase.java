package org.rockhill.adorApp.web.controller.helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpSession;

public class ControllerBase {

    public static final String JSON_RESPONSE_UPDATE = "entityUpdate";

    public Boolean isAdoratorAdmin(CurrentUserProvider currentUserProvider, HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        return currentUserInformationJson.isAdoratorAdmin;
    }

    protected HttpHeaders setHeadersForJSON() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return responseHeaders;
    }

    protected String getJsonString(final String id, final Object object) {
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(id, gson.toJsonTree(object));
        String json = gson.toJson(jsonObject);
        return json;
    }

}
