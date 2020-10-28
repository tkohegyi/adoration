package org.rockhill.adoration.web.controller.helper;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

/**
 * Base class of every controller.
 */
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

    /**
     * Returns with a String version of a Json object.
     *
     * Like:
     *  { id : { the object } }
     * @param id is the element id
     * @param object is the object
     * @return with the string version of the Json object
     */
    protected String getJsonString(final String id, final Object object) {
        String json;
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(id, gson.toJsonTree(object));
        json = gson.toJson(jsonObject);
        return json;
    }

    protected ResponseEntity<String> buildResponseBodyResult(final String resultId, final Object resultObject, final HttpStatus httpStatus) {
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        result = new ResponseEntity<>(getJsonString(resultId, resultObject), responseHeaders, httpStatus);
        return result;
    }

    protected ResponseEntity<String> buildUnauthorizedActionBodyResult() {
        ResponseEntity<String> result;
        result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, UNAUTHORIZED_ACTION, HttpStatus.FORBIDDEN);
        return result;
    }

}
