package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.service.AuthenticatedUser;
import org.rockhill.adorApp.web.service.FacebookUser;
import org.rockhill.adorApp.web.service.GoogleUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Component
public class CurrentUserProvider {

    public CurrentUserInformationJson getUserInformation(HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = new CurrentUserInformationJson();

        Authentication authentication = null;
        SecurityContext securityContext = (SecurityContext) httpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        if (securityContext != null) {
            authentication = securityContext.getAuthentication();
        }
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            String loggedInUserName;
            Person person = null;
            if (principal instanceof AuthenticatedUser) {
                AuthenticatedUser user = (AuthenticatedUser) principal;
                if (user.isSessionValid()) {
                    user.extendSessionTimeout();
                    currentUserInformationJson.isLoggedIn = true;  // if authentication is not null then the person is logged in
                    person = user.getPerson();
                    if (person != null) {
                        loggedInUserName = person.getName();
                    } else {
                        loggedInUserName = "Guest User - Anonymous";
                        if (principal instanceof GoogleUser) {
                            loggedInUserName = "Guest User - " + user.getSocial().getGoogleUserName();
                        }
                        if (principal instanceof FacebookUser) {
                            loggedInUserName = "Guest User - " + user.getSocial().getFacebookUserName();
                        }
                    }
                    currentUserInformationJson.loggedInUserName = loggedInUserName; //user who logged in via social
                    currentUserInformationJson.userName = loggedInUserName; //user who registered as adorator (his/her name may differ from the username used in Social)
                    if (person != null) {
                        currentUserInformationJson.isAuthorized = true; //not just logged in, but since the person is known, authorized too
                        currentUserInformationJson.id = person.getId();
                        currentUserInformationJson.userName = person.getName();
                        //TODO setup authorized access list
                    }
                } else {
                    //session expired!
                    securityContext.setAuthentication(null); // this cleans up the authentication data technically
                    httpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY); // this clean up the session itself
                }
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
