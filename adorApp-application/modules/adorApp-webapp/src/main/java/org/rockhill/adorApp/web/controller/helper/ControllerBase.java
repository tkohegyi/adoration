package org.rockhill.adorApp.web.controller.helper;

import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;

import javax.servlet.http.HttpSession;

public class ControllerBase {

    public Boolean isAdoratorAdmin(CurrentUserProvider currentUserProvider, HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        return currentUserInformationJson.isAdoratorAdmin;
    }
}
