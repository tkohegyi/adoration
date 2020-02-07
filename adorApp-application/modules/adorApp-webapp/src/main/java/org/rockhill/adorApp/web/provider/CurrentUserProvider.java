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
    private Set<AdoratorStatusTypes> registeredAdorator;
    private Set<AdoratorStatusTypes> leaders;
    private Set<AdoratorStatusTypes> admins;

    CurrentUserProvider() {
        registeredAdorator = new HashSet<>();
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR_MAIN_COORDINATOR);
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR_DAILY_COORDINATOR);
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR_HOURLY_COORDINATOR);
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR_SPIRITUAL_COORDINATOR);
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR);

        leaders = new HashSet<>();
        leaders.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        leaders.add(AdoratorStatusTypes.ADORATOR_MAIN_COORDINATOR);
        leaders.add(AdoratorStatusTypes.ADORATOR_DAILY_COORDINATOR);
        leaders.add(AdoratorStatusTypes.ADORATOR_HOURLY_COORDINATOR);
        leaders.add(AdoratorStatusTypes.ADORATOR_SPIRITUAL_COORDINATOR);

        admins = new HashSet<>();
        admins.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        admins.add(AdoratorStatusTypes.ADORATOR_MAIN_COORDINATOR);
    }

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
            Person person; // = null;
            if (principal instanceof AuthenticatedUser) {
                AuthenticatedUser user = (AuthenticatedUser) principal;
                if (user.isSessionValid()) {
                    user.extendSessionTimeout();
                    currentUserInformationJson.isLoggedIn = true;  // if authentication is not null then the person is logged in
                    person = user.getPerson();
                    if (person != null) {
                        loggedInUserName = person.getName();
                    } else {
                        loggedInUserName = "Vend\u00e9g - Anonymous";
                        if (principal instanceof GoogleUser) {
                            loggedInUserName = "Vend\u00e9g - " + user.getSocial().getGoogleUserName();
                        }
                        if (principal instanceof FacebookUser) {
                            loggedInUserName = "Vend\u00e9g - " + user.getSocial().getFacebookUserName();
                        }
                    }
                    currentUserInformationJson.loggedInUserName = loggedInUserName; //user who logged in via social
                    currentUserInformationJson.userName = loggedInUserName; //user who registered as adorator (his/her name may differ from the username used in Social)
                    if (person != null) {
                        currentUserInformationJson.isAuthorized = true; //not just logged in, but since the person is known, authorized too
                        currentUserInformationJson.id = person.getId();
                        currentUserInformationJson.userName = person.getName();
                        AdoratorStatusTypes status = AdoratorStatusTypes.getAdoratorStatusTypeFromId(person.getAdorationStatus());
                        currentUserInformationJson.isRegisteredAdorator = registeredAdorator.contains(status);
                        currentUserInformationJson.isAdoratorLeader = leaders.contains(status);
                        currentUserInformationJson.isAdoratorAdmin = admins.contains(status);
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
    
}
