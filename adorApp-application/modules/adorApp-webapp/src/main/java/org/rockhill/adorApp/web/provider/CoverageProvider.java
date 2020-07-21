package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithAuditTrail;
import org.rockhill.adorApp.database.business.BusinessWithLink;
import org.rockhill.adorApp.database.business.BusinessWithNextGeneralKey;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.BusinessWithTranslator;
import org.rockhill.adorApp.database.business.helper.enums.AdorationMethodTypes;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CoverageInformationJson;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
import org.rockhill.adorApp.web.json.PersonCommitmentJson;
import org.rockhill.adorApp.web.json.PersonJson;
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
        //determine if adorator info is required
        boolean canSeeAdorators = currentUserInformationJson.isLoggedIn && currentUserInformationJson.isAdoratorLeader;

        //fill the day names first
        coverageInformationJson.dayNames = new HashMap<>();
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            String textId = dayName.toString();
            String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
            coverageInformationJson.dayNames.put(textId.toLowerCase(), value);
        }

        //fill the hour coverage information
        List<Link> linkList = businessWithLink.getLinkList(BusinessWithLink.WITHOUT_COORDINATORS);
        coverageInformationJson.visibleHours = new HashMap<>();
        coverageInformationJson.allHours = new HashMap<>();
        coverageInformationJson.onlineHours = new HashMap<>();
        coverageInformationJson.adorators = new HashMap<>();
        //ensure that we have initial info about all the hours
        for (int i = 0; i < 168; i++) {
            coverageInformationJson.visibleHours.put(i, 0);
            coverageInformationJson.allHours.put(i, new HashSet<>());
            coverageInformationJson.onlineHours.put(i, new HashSet<>());
        }
        for (Link link : linkList) {
            Integer hourId = link.getHourId();
            if (AdorationMethodTypes.getTypeFromId(link.getType()) == AdorationMethodTypes.PHYSICAL) {
                //fill all hours first, if that is necessary
                if (coverageInformationJson.allHours.containsKey(hourId)) {
                    //we already have this in the map
                    if (canSeeAdorators) {
                        Set<Long> idSet = coverageInformationJson.allHours.get(hourId);
                        idSet.add(link.getPersonId());
                    }
                } else {
                    //we don't have this in our map, data error !
                    logger.warn("Unexpected row in Link table, with Id:" + link.getId());
                }
                //fill visible hours
                if (link.getPriority() < 3) { //for physical we ask priority 1,2 too
                    if (coverageInformationJson.visibleHours.containsKey(hourId)) {
                        //we already have this in the map
                        coverageInformationJson.visibleHours.put(hourId, coverageInformationJson.visibleHours.get(hourId) + 1);
                    } //else part already handled at allHours
                }
            }
            if (AdorationMethodTypes.getTypeFromId(link.getType()) == AdorationMethodTypes.ONLINE) {
                if (coverageInformationJson.onlineHours.containsKey(hourId)) {
                    //we already have this in the map
                    Set<Long> idSet = coverageInformationJson.onlineHours.get(hourId);
                    if (canSeeAdorators) {
                        idSet.add(link.getPersonId()); //we add real ids only if user can see them
                    } else { //otherwise we use a fake id
                        idSet.add(Long.valueOf(0));
                    }
                } else {
                    //we don't have this in our map, data error !
                    logger.warn("Unexpected row in Link table, with Id:" + link.getId());
                }
            }
            //fill adorator info, now only for leaders (for the rest it is problematic, who can see what)
            if (canSeeAdorators) {
                Person p = businessWithPerson.getPersonById(link.getPersonId());
                if (p != null && !coverageInformationJson.adorators.containsKey(p.getId())) {
                    PersonJson personJson = new PersonJson(p, currentUserInformationJson.isPrivilegedUser());
                    coverageInformationJson.adorators.put(p.getId(), personJson);
                }
            }
        }
        return coverageInformationJson;
    }

    public Object getPersonCommitmentAsObject(Long id, String languageCode) {
        PersonCommitmentJson personCommitmentJson = new PersonCommitmentJson();
        List<Link> linkList = businessWithLink.getLinkList(BusinessWithLink.WITH_COORDINATORS);
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

    private AuditTrail prepareUpdateAuditTrail(Long id, Long linkId, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Link:Update:" + linkId.toString(), fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
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
                    "Link:New:" + l.getId().toString(), "Day: " + businessWithLink.getDayNameFromHourId(l.getHourId())
                            + ", Hour: " + businessWithLink.getHourFromHourId(l.getHourId()),
                    "Type: " + AdorationMethodTypes.getTranslatedString(l.getType())
                            + ", Priority: " + l.getPriority().toString());
            auditTrailCollection.add(auditTrail);
            Long id = businessWithLink.newLink(l, auditTrailCollection);
            return id;
        } else {
            oldLink = businessWithLink.getLink(l.getId());
            if (oldLink == null) {
                logger.info("User:" + currentUserInformationJson.userName + " tried to update a not existing Person Commitment/Link");
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
        if (!newInt.equals(oldInt)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(l.getPersonId(), l.getId(), currentUserInformationJson.userName,"Day/Hour",
                    businessWithLink.getDayNameFromHourId(oldLink.getHourId()) + "/" + businessWithLink.getHourFromHourId(oldLink.getHourId()),
                    businessWithLink.getDayNameFromHourId(l.getHourId()) + "/" + businessWithLink.getHourFromHourId(l.getHourId())));
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
        oldInt = oldLink.getPriority();
        if ((newInt < 0) || (newInt > 25)) { //need to synch with applog.jsp
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Link with bad priority.");
            return null;
        }
        if (!newInt.equals(oldInt)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(l.getPersonId(), l.getId(), currentUserInformationJson.userName,"Priority", oldInt.toString(), newInt.toString()));
        }
        //admincomment
        String newValue = l.getAdminComment();
        String oldValue = oldLink.getAdminComment();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        if (!newValue.contentEquals(oldValue)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(l.getPersonId(), l.getId(), currentUserInformationJson.userName,"Admin Comment", oldValue, newValue));
        }
        //publicComment
        newValue = l.getPublicComment();
        oldValue = oldLink.getPublicComment();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        if (!newValue.contentEquals(oldValue)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(l.getPersonId(), l.getId(), currentUserInformationJson.userName,"Public Comment", oldValue, newValue));
        }
        //type
        newInt = l.getType();
        oldInt = oldLink.getType();
        if ((newInt < 0) || (newInt > 3)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Link with bad type.");
            return null;
        }
        if (!newInt.equals(oldInt)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(l.getPersonId(), l.getId(), currentUserInformationJson.userName,"Type",
                    AdorationMethodTypes.getTranslatedString(oldInt), AdorationMethodTypes.getTranslatedString(newInt)));
        }

        Long id = businessWithLink.updateLink(l, auditTrailCollection);
        return id;
    }

    public Long deletePersonCommitment(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        Long id = Long.parseLong(p.entityId);
        Link l = businessWithLink.getLink(id);
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(l.getPersonId(), currentUserInformationJson.userName,
                "Link:Delete:" + l.getId().toString(), "Day/Hour: " + businessWithLink.getDayNameFromHourId(l.getHourId())
                        + "/" + businessWithLink.getHourFromHourId(l.getHourId()),
                "Type: " + AdorationMethodTypes.getTranslatedString(l.getType())
                        + ", Priority: " + l.getPriority().toString());
        id = businessWithLink.deleteLink(l, auditTrail);
        return id;
    }
}
