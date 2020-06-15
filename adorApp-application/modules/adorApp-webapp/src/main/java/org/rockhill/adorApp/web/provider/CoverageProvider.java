package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.*;
import org.rockhill.adorApp.database.business.helper.enums.AdorationMethodTypes;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.business.helper.enums.WebStatusTypes;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Translator;
import org.rockhill.adorApp.web.json.CoverageInformationJson;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.PersonCommitmentJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CoverageProvider {

    private final Logger logger = LoggerFactory.getLogger(CoverageProvider.class);


    @Autowired
    BusinessWithTranslator businessWithTranslator;
    @Autowired
    BusinessWithLink businessWithLink;
    @Autowired
    BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    BusinessWithPerson businessWithPerson;

    public CoverageInformationJson getCoverageInfo(CurrentUserInformationJson currentUserInformationJson) {
        CoverageInformationJson coverageInformationJson = new CoverageInformationJson();

        //fill the day names first
        List<Translator> translatorList = businessWithTranslator.getTranslatorList(currentUserInformationJson.languageCode);
        coverageInformationJson.dayNames = new HashMap<>();
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            String textId = dayName.toString();
            String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
            coverageInformationJson.dayNames.put(textId.toLowerCase(), value);
        }

        //fill the hour coverage information
        List<Link> linkList = businessWithLink.getLinkList();
        coverageInformationJson.hours = new HashMap<>();
        coverageInformationJson.onlineHours = new HashMap<>();
        //ensure that we have initial info about all the hours
        for (int i = 0; i < 168; i++) {
            coverageInformationJson.hours.put(Integer.valueOf(i), 0);
            coverageInformationJson.onlineHours.put(Integer.valueOf(i), 0);
        }
        for (Link link : linkList) {
            Integer hourId = link.getHourId();
            if (AdorationMethodTypes.getTypeFromId(link.getType()) == AdorationMethodTypes.PHYSICAL) {
                if (coverageInformationJson.hours.containsKey(hourId)) {
                    //we already have this in the map
                    coverageInformationJson.hours.put(hourId, coverageInformationJson.hours.get(hourId) + 1);
                } else {
                    //we don't have this in our map, data error !
                    logger.warn("Unexpected row in Link table, with Id:" + link.getId());
                }
            } else {//AdorationMethodTypes.ONLINE
                if (coverageInformationJson.onlineHours.containsKey(hourId)) {
                    //we already have this in the map
                    coverageInformationJson.onlineHours.put(hourId, coverageInformationJson.onlineHours.get(hourId) + 1);
                } else {
                    //we don't have this in our map, data error !
                    logger.warn("Unexpected row in Link table, with Id:" + link.getId());
                }
            }
        }
        return coverageInformationJson;
    }

    public Object getPersonCommitmentAsObject(Long id, String languageCode) {
        PersonCommitmentJson personCommitmentJson = new PersonCommitmentJson();
        List<Link> linkList = businessWithLink.getLinkList();
        Set<Integer> committedHours = new HashSet<>();
        //first fill hours of the person
        for (Link link : linkList) {
            if (link.getPersonId().equals(id)) {
                committedHours.add(link.getHourId());
                personCommitmentJson.linkedHours.add(link);
            }
        }
        //now fill others who help for the person at his/her hours - exclude own hours
        for (Link link : linkList) {
            if (committedHours.contains(link.getHourId()) && !link.getPersonId().equals(id)) {
                personCommitmentJson.others.add(link);
            }
        }
        //fill dayNames since we need to decode it from hourId
        for (TranslatorDayNames dayName: TranslatorDayNames.values()) {
            personCommitmentJson.dayNames.add(businessWithTranslator.getTranslatorValue(languageCode, dayName.toString(), "unknown"));
        }
        return personCommitmentJson;
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Link::Update", fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    public Long updatePersonCommitment(Link l, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Link oldLink = l;
        l.setAdminComment(l.getAdminComment().trim());
        l.setPublicComment(l.getPublicComment().trim());
        if (l.getId() == 0) {
            //new Link
            l.setId(businessWithNextGeneralKey.getNextGeneralId());
            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,
                    "Link::New", "Day: " + businessWithLink.getDayNameFromHourId(l.getHourId())
                            + ", Hour: " + businessWithLink.getHourFromHourId(l.getHourId()),
                    "Type: " + AdorationMethodTypes.getTranslatedString(l.getType())
                            + ", Priority: " + l.getPriority().toString());
            auditTrailCollection.add(auditTrail);
            Long id = businessWithLink.newLink(l, auditTrailCollection);
            return id;
        } else {
            oldLink = businessWithLink.getLink(l.getId());
            if (oldLink == null) {
                logger.info("User:" + currentUserInformationJson.userName + " tried to update not existing Person Commitment/Link");
                return null;
            }
        }
        //hourid
        Integer newInt = l.getHourId();
        Integer oldInt = oldLink.getHourId();
        if ((newInt < 0) || (newInt > 167)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Link with bad hour id:\""
                    + newInt.toString() + "\".");
            return null;
        }
        if (newInt != oldInt) {
            prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,"Day",
                    businessWithLink.getDayNameFromHourId(oldLink.getHourId()), businessWithLink.getDayNameFromHourId(l.getHourId()));
            prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,"Hour",
                    businessWithLink.getHourFromHourId(oldLink.getHourId()), businessWithLink.getHourFromHourId(l.getHourId()));
        }
        //personId
        Person p = businessWithPerson.getPersonById(l.getPersonId());
        if (p == null) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Link for a non-existing Person.");
            return null;
        }
        if (!l.getPersonId().equals(oldLink.getPersonId())) {
            //changing person is not supported so this request must be rouge
            logger.info("User:" + currentUserInformationJson.userName + " tried to change Person for Link:" + l.getId());
            return null;
        }
        //priority
        newInt = l.getPriority();
        if ((newInt < 0) || (newInt > 25)) { //need to synch with applog.jsp
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Link with bad priority.");
            return null;
        }
        if (newInt != oldInt) {
            prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,"Priority", oldInt.toString(), newInt.toString());
        }
        //admincomment
        String newValue = l.getAdminComment();
        String oldValue = oldLink.getAdminComment();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        if (!newValue.contentEquals(oldValue)) {
            prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,"Admin Comment", oldValue, newValue);
        }
        //publicComment
        newValue = l.getPublicComment();
        oldValue = oldLink.getPublicComment();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        if (!newValue.contentEquals(oldValue)) {
            prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,"Public Comment", oldValue, newValue);
        }
        //type
        newInt = l.getType();
        oldInt = oldLink.getType();
        if ((newInt < 0) || (newInt > 1)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Link with bad type.");
            return null;
        }
        if (newInt != oldInt) {
            prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,"Type",
                    AdorationMethodTypes.getTranslatedString(oldInt), AdorationMethodTypes.getTranslatedString(newInt));
        }

        Long id = businessWithLink.updateLink(l, auditTrailCollection);
        return id;
    }
}
