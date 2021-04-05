package org.rockhill.adoration.web.provider;

import org.rockhill.adoration.database.business.BusinessWithLink;
import org.rockhill.adoration.database.business.BusinessWithPerson;
import org.rockhill.adoration.database.business.BusinessWithSocial;
import org.rockhill.adoration.database.business.BusinessWithTranslator;
import org.rockhill.adoration.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adoration.database.business.helper.enums.SocialStatusTypes;
import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.GuestInformationJson;
import org.rockhill.adoration.web.json.InformationJson;
import org.rockhill.adoration.web.json.PersonJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class to provide Information about users.
 */
@Component
public class InformationProvider {

    private final Logger logger = LoggerFactory.getLogger(InformationProvider.class);

    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithSocial businessWithSocial;
    @Autowired
    private BusinessWithLink businessWithLink;
    @Autowired
    private BusinessWithTranslator businessWithTranslator;
    @Autowired
    private CoordinatorProvider coordinatorProvider;

    /**
     * Get overall information about a registered adorator.
     *
     * @return with the info in json object form
     */
    public Object getInformation(CurrentUserInformationJson currentUserInformationJson) {
        InformationJson informationJson = new InformationJson();
        //get name and status
        Long personId = currentUserInformationJson.personId;
        Person person = businessWithPerson.getPersonById(personId);
        if (person == null) {
            //wow, we should not be here
            logger.warn("User got access to prohibited area: {}", currentUserInformationJson.loggedInUserName);
            informationJson.error = "access denied";
        } else {
            //we have person info
            informationJson.name = person.getName();
            informationJson.status = AdoratorStatusTypes.getTranslatedString(person.getAdorationStatus());
            informationJson.id = person.getId().toString();
            informationJson.linkList = businessWithLink.getLinksOfPerson(person);
            informationJson.leadership = coordinatorProvider.getLeadership(currentUserInformationJson);
            Calendar cal = Calendar.getInstance();
            int hourId = (cal.get(Calendar.DAY_OF_WEEK) - 1) * BusinessWithLink.HOUR_IN_A_DAY + cal.get(Calendar.HOUR_OF_DAY);  // use sun as 0 day
            informationJson.hourInDayNow = hourId % BusinessWithLink.HOUR_IN_A_DAY;
            informationJson.hourInDayNext = (hourId + 1) % BusinessWithLink.HOUR_IN_A_DAY;
            informationJson.currentHourList = businessWithLink.getLinksOfHour(hourId);
            informationJson.futureHourList = businessWithLink.getLinksOfHour((hourId + 1) % (Link.MAX_HOUR + 1));
            fillRelatedPersonIds(informationJson, currentUserInformationJson.isPrivilegedUser());
            //fill the day names first
            informationJson.dayNames = new HashMap<>();
            for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
                String textId = dayName.toString();
                String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
                informationJson.dayNames.put(dayName.getDayValue(), value);
            }

        }
        return informationJson;
    }

    private void fillRelatedPersonIds(InformationJson informationJson, boolean isPrivilegedUser) {
        //NOTE: linkList, currentHourList and futureHourList must be filled already
        Set<Long> personIds = new HashSet<>();
        if (informationJson.linkList != null) {
            for (Link l : informationJson.linkList) {
                personIds.add(l.getPersonId());
            }
        }
        if (informationJson.currentHourList != null) {
            for (Link l : informationJson.currentHourList) {
                personIds.add(l.getPersonId());
            }
        }
        if (informationJson.futureHourList != null) {
            for (Link l : informationJson.futureHourList) {
                personIds.add(l.getPersonId());
            }
        }
        Iterator<Long> ppl = personIds.iterator();
        List<PersonJson> relatedPersonList = new LinkedList<>();
        while (ppl.hasNext()) {
            Long id = ppl.next();
            Person p = businessWithPerson.getPersonById(id);
            if (p != null) {
                relatedPersonList.add(new PersonJson(p, isPrivilegedUser));
            } else {
                logger.warn("Person ID usage found without real Person, id: {}", id);
            }
        }
        informationJson.relatedPersonList = relatedPersonList;
    }

    /**
     * Get information for a guest.
     *
     * @return with the info in json object form
     */
    public Object getGuestInformation(CurrentUserInformationJson currentUserInformationJson) {
        GuestInformationJson guestInformationJson = new GuestInformationJson();
        //get name and status
        Long socialId = currentUserInformationJson.socialId;
        Social social = businessWithSocial.getSocialById(socialId);
        if (social == null) {
            //wow, we should not be here
            logger.warn("Guest User got access to prohibited area: {}", currentUserInformationJson.loggedInUserName);
            guestInformationJson.error = "access denied";
        } else {
            //we have social info
            if (social.getFacebookUserId().length() > 0) {
                guestInformationJson.isFacebook = true;
                guestInformationJson.emailFacebook = social.getFacebookEmail();
                guestInformationJson.nameFacebook = social.getFacebookUserName();
            } else {
                guestInformationJson.isFacebook = false;
            }
            if (social.getGoogleUserId().length() > 0) {
                guestInformationJson.isGoogle = true;
                guestInformationJson.emailGoogle = social.getGoogleEmail();
                guestInformationJson.nameGoogle = social.getGoogleUserName();
            } else {
                guestInformationJson.isGoogle = false;
            }
            switch (SocialStatusTypes.getTypeFromId(social.getSocialStatus())) {
            case IDENTIFIED_USER: //adoráló - we should be be here
                guestInformationJson.status = "Regisztrált adoráló.";
                break;
            case SOCIAL_USER: //guest
                guestInformationJson.status = "Vendég felhasználó.";
                break;
            default:
            case WAIT_FOR_IDENTIFICATION: //waiting for identification
                guestInformationJson.status
                        = "Nem azonosított felhasználó. "
                        + "Kérjük írja meg elérhetőségét a koordinátoroknak, a lentebb található Üzenetküldés segítségével, "
                        + "hogy az azonosítás megtörténhessen.";
                break;
            }
            guestInformationJson.leadership = coordinatorProvider.getLeadership(currentUserInformationJson);
            guestInformationJson.socialServiceUsed = currentUserInformationJson.socialServiceUsed;
        }
        return guestInformationJson;
    }
}
