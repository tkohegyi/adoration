package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithAuditTrail;
import org.rockhill.adorApp.database.business.BusinessWithCoordinator;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Coordinator;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.service.AuthenticatedUser;
import org.rockhill.adorApp.web.service.FacebookUser;
import org.rockhill.adorApp.web.service.GoogleUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

@Component
public class CurrentUserProvider {

    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;

    @Autowired
    BusinessWithCoordinator businessWithCoordinator;

    private final Set<AdoratorStatusTypes> registeredAdorator;
    private final Set<AdoratorStatusTypes> leaders;
    private final Set<AdoratorStatusTypes> admins;

    CurrentUserProvider() {
        registeredAdorator = new HashSet<>();
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR);
        registeredAdorator.add(AdoratorStatusTypes.ADORATOR_EMPHASIZED);

        leaders = new HashSet<>();
        leaders.add(AdoratorStatusTypes.ADORATOR_ADMIN);
        leaders.add(AdoratorStatusTypes.ADORATOR_EMPHASIZED);

        admins = new HashSet<>();
        admins.add(AdoratorStatusTypes.ADORATOR_ADMIN);
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
            String userName;
            Person person; // = null;
            Social social; // = null;
            if (principal instanceof AuthenticatedUser) {
                AuthenticatedUser user = (AuthenticatedUser) principal;
                if (user.isSessionValid()) {
                    user.extendSessionTimeout();
                    currentUserInformationJson.isLoggedIn = true;  // if authentication is not null then the person is logged in
                    currentUserInformationJson.coordinatorId = -1;
                    currentUserInformationJson.isHourlyCoordinator = false;
                    currentUserInformationJson.isDailyCoordinator = false;
                    person = user.getPerson();
                    if (person != null) {
                        Coordinator coordinator = businessWithCoordinator.getCoordinatorFromPersonId(person.getId());
                        if (coordinator != null) {
                            currentUserInformationJson.coordinatorId = coordinator.getCoordinatorType();
                            currentUserInformationJson.isHourlyCoordinator = currentUserInformationJson.coordinatorId < 24;
                            currentUserInformationJson.isDailyCoordinator = !currentUserInformationJson.isHourlyCoordinator;
                        }
                        loggedInUserName = person.getName();
                        userName = loggedInUserName;
                    } else {
                        userName = "Anonymous";
                        loggedInUserName = "Vend\u00e9g - " + userName;
                        if (principal instanceof GoogleUser) {
                            userName = user.getSocial().getGoogleUserName();
                            loggedInUserName = "Vend\u00e9g - " + userName;
                            currentUserInformationJson.socialServiceUsed = "Google";
                        }
                        if (principal instanceof FacebookUser) {
                            userName = user.getSocial().getFacebookUserName();
                            loggedInUserName = "Vend\u00e9g - " + userName;
                            currentUserInformationJson.socialServiceUsed = "Facebook";
                        }
                    }
                    currentUserInformationJson.loggedInUserName = loggedInUserName; //user who logged in via social
                    currentUserInformationJson.userName = userName; //user who registered as adorator (his/her name may differ from the username used in Social)
                    social = user.getSocial();
                    if (social != null) {
                        currentUserInformationJson.socialId = social.getId();
                        String email = social.getGoogleEmail();
                        if (email.length() == 0) {
                            email = social.getFacebookEmail();
                        }
                        currentUserInformationJson.socialEmail = email;
                    }
                    if (person != null) {
                        currentUserInformationJson.isAuthorized = true; //not just logged in, but since the person is known, authorized too
                        currentUserInformationJson.personId = person.getId();
                        currentUserInformationJson.userName = person.getName();
                        AdoratorStatusTypes status = AdoratorStatusTypes.getTypeFromId(person.getAdorationStatus());
                        currentUserInformationJson.isRegisteredAdorator = registeredAdorator.contains(status);
                        currentUserInformationJson.isPrivilegedAdorator = leaders.contains(status) || currentUserInformationJson.coordinatorId > -1;
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

    public String getQuickUserName(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        String loggedInUserName = "";
        Person person; // = null;
        if (principal instanceof AuthenticatedUser) {
            AuthenticatedUser user = (AuthenticatedUser) principal;
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
        }
        return loggedInUserName;
    }

    public void registerLogin(HttpSession httpSession, final String oauth2ServiceName) {
        CurrentUserInformationJson currentUserInformationJson = getUserInformation(httpSession);
        String data = oauth2ServiceName;
        long socialId = 0;
        if (currentUserInformationJson.socialId != null) {
            socialId = currentUserInformationJson.socialId;
        } else {
            data = "Unidentified Social data.";
        }
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(socialId,
                currentUserInformationJson.userName, "Login", "User logged in: " + currentUserInformationJson.userName, data);
        businessWithAuditTrail.saveAuditTrailSafe(auditTrail);
    }

    public void registerLogout(HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = getUserInformation(httpSession);
        String data = "";
        long socialId = 0;
        if (currentUserInformationJson.socialId != null) {
            socialId = currentUserInformationJson.socialId;
        } else {
            data = "Unidentified Social data.";
        }
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(socialId,
                currentUserInformationJson.userName, "Logout", "User logged out: " + currentUserInformationJson.userName, data);
        businessWithAuditTrail.saveAuditTrailSafe(auditTrail);
    }
}
