package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithPeople;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.tables.People;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CurrentUserProvider {

    @Autowired
    BusinessWithPeople businessWithPeople;

    public CurrentUserInformationJson getUserInformation() {
        CurrentUserInformationJson currentUserInformationJson = new CurrentUserInformationJson();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String loggedInUserName;
        if (principal instanceof UserDetails) {
            loggedInUserName = ((UserDetails) principal).getUsername();
        } else {
            loggedInUserName = principal.toString();
        }
        currentUserInformationJson.loggedInUserName = loggedInUserName;
        People people = businessWithPeople.getPersonByEmail(loggedInUserName);
        if (people != null) {
            currentUserInformationJson.isLoggedIn = true;
            currentUserInformationJson.id = people.getId();
            currentUserInformationJson.userName = people.getName();
            //TODO - fil full currentUserInfo + exten the structure itself
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
