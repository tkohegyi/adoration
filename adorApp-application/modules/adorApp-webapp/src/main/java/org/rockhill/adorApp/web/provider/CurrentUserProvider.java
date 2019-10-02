package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.service.GoogleUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CurrentUserProvider {

    public CurrentUserInformationJson getUserInformation(SecurityContext securityContext) {
        CurrentUserInformationJson currentUserInformationJson = new CurrentUserInformationJson();

        Authentication authentication = null;
        if (securityContext != null) {
            authentication = securityContext.getAuthentication();
        }
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            String loggedInUserName;
            Person person = null;
            if (principal instanceof GoogleUser) {
                currentUserInformationJson.isLoggedIn = true;  // if authentication is not null then the person is logged in
                GoogleUser googleUser = (GoogleUser) principal;
                person = googleUser.getPerson();
                if (person != null) {
                    loggedInUserName = person.getName();
                } else {
                    loggedInUserName = "Guest User - " + googleUser.getSocial().getGoogleUserName();
                }
            } else {
                loggedInUserName = "Unknown";
            }
            currentUserInformationJson.loggedInUserName = loggedInUserName; //user who logged in via social
            currentUserInformationJson.userName = loggedInUserName; //user who registered as adorator (his/her name may differ from the username used in Social)
            if (person != null) {
                currentUserInformationJson.isAuthorized = true; //not just logged in, but since the person is known, authorized too
                currentUserInformationJson.id = person.getId();
                currentUserInformationJson.userName = person.getName();
                //TODO setup authorized access list
            }
        }
        return currentUserInformationJson;
    }

    private boolean getMenuAccessToAppLog(AdoratorStatusTypes currentUserType) {
        Set<AdoratorStatusTypes> configurators = new HashSet<>();
        configurators.add(AdoratorStatusTypes.ADORATOR_MAIN_COORDINATOR);
        configurators.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        return configurators.contains(currentUserType);
    }

}
