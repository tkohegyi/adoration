package org.rockhill.adoration.web.provider;

import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.database.business.BusinessWithLink;
import org.rockhill.adoration.database.business.BusinessWithNextGeneralKey;
import org.rockhill.adoration.database.business.BusinessWithPerson;
import org.rockhill.adoration.database.business.BusinessWithTranslator;
import org.rockhill.adoration.database.business.helper.enums.AdorationMethodTypes;
import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;
import org.rockhill.adoration.database.tables.AuditTrail;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.web.json.CoverageInformationJson;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.DeleteEntityJson;
import org.rockhill.adoration.web.json.PersonCommitmentJson;
import org.rockhill.adoration.web.json.PersonJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to provide Coverage related information.
 */
@Component
public class CoverageProvider {

    private static final String USER = "User:";
    private static final String CANNOT_UPDATE_PERSON_COMMITMENT_TEXT = "Cannot update the Person Commitment, please check the values and retry.";
    private static final Integer MAX_PRIORITY = 25;
    private final Logger logger = LoggerFactory.getLogger(CoverageProvider.class);

    @Autowired
    private BusinessWithTranslator businessWithTranslator;
    @Autowired
    private BusinessWithLink businessWithLink;
    @Autowired
    private BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private BusinessWithPerson businessWithPerson;

    /**
     * Get information about the coverage for the specific person.
     *
     * @param currentUserInformationJson identifies the actual user
     * @return with the coverage information in json
     */
    public CoverageInformationJson getCoverageInfo(CurrentUserInformationJson currentUserInformationJson) {
        CoverageInformationJson coverageInformationJson = new CoverageInformationJson();

        //fill the day names first
        coverageInformationJson.dayNames = new HashMap<>();
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            String textId = dayName.toString();
            String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
            coverageInformationJson.dayNames.put(textId.toLowerCase(), value);
        }

        //fill the hour coverage information
        coverageInformationJson.visibleHours = new HashMap<>();
        coverageInformationJson.allHours = new HashMap<>();
        coverageInformationJson.onlineHours = new HashMap<>();
        coverageInformationJson.adorators = new HashMap<>();
        //ensure that we have initial info about all the hours
        for (int i = 0; i <= BusinessWithLink.MAX_HOUR; i++) {
            coverageInformationJson.visibleHours.put(i, 0);
            coverageInformationJson.allHours.put(i, new HashSet<>());
            coverageInformationJson.onlineHours.put(i, new HashSet<>());
        }
        fillCoverage(currentUserInformationJson, coverageInformationJson);
        return coverageInformationJson;
    }

    private void fillCoverage(CurrentUserInformationJson currentUserInformationJson, CoverageInformationJson coverageInformationJson) {
        //determine if adorator info is required
        boolean canSeeAdorators = currentUserInformationJson.isLoggedIn && currentUserInformationJson.isPrivilegedAdorator;
        List<Link> linkList = businessWithLink.getLinkList();
        for (Link link : linkList) {
            handleLinkWithPhysicalAdorator(link, coverageInformationJson, canSeeAdorators);
            handleLinkWithOnlineAdorator(link, coverageInformationJson, canSeeAdorators);
            handleAdoratorOfLink(link, coverageInformationJson, canSeeAdorators, currentUserInformationJson.isPrivilegedUser());
        }
    }

    private void handleAdoratorOfLink(Link link, CoverageInformationJson coverageInformationJson, boolean canSeeAdorators, boolean isPrivilegedUser) {
        //fill adorator info, now only for leaders (for the rest it is problematic, who can see what)
        if (canSeeAdorators) {
            Person p = businessWithPerson.getPersonById(link.getPersonId());
            if (p != null && !coverageInformationJson.adorators.containsKey(p.getId())) {
                PersonJson personJson = new PersonJson(p, isPrivilegedUser);
                coverageInformationJson.adorators.put(p.getId(), personJson);
            }
        }
    }

    private void handleLinkWithPhysicalAdorator(Link link, CoverageInformationJson coverageInformationJson, boolean canSeeAdorators) {
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
                logger.warn("Unexpected row in Link table, with Id: {}", link.getId());
            }
            //fill visible hours
            if ((link.getPriority() < BusinessWithLink.PRIORITY_BORDER) //for physical we ask priority 1,2 too
                    && coverageInformationJson.visibleHours.containsKey(hourId)) {
                //we already have this in the map
                coverageInformationJson.visibleHours.put(hourId, coverageInformationJson.visibleHours.get(hourId) + 1);
            } //else part already handled at allHours
        }
    }

    private void handleLinkWithOnlineAdorator(Link link, CoverageInformationJson coverageInformationJson, boolean canSeeAdorators) {
        Integer hourId = link.getHourId();
        if (AdorationMethodTypes.getTypeFromId(link.getType()) == AdorationMethodTypes.ONLINE) {
            if (coverageInformationJson.onlineHours.containsKey(hourId)) {
                //we already have this in the map
                Set<Long> idSet = coverageInformationJson.onlineHours.get(hourId);
                if (canSeeAdorators) {
                    idSet.add(link.getPersonId()); //we add real ids only if user can see them
                } else { //otherwise we use a fake id
                    idSet.add(0L);
                }
            } else {
                //we don't have this in our map, data error !
                logger.warn("Unexpected row in Link table, with Id: {}", link.getId());
            }
        }
    }

    /**
     * Get person commitment information (the hours when the person performs adoration) as json object.
     *
     * @param id           identifies the person
     * @param languageCode of the actual user
     * @return with the json object
     */
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
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            personCommitmentJson.dayNames.add(businessWithTranslator.getTranslatorValue(languageCode, dayName.toString(), "unknown"));
        }
        return personCommitmentJson;
    }

    private AuditTrail prepareUpdateAuditTrail(Long id, Long linkId, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Link:Update:" + linkId.toString(),
                fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    private Long createPersonCommitment(CurrentUserInformationJson currentUserInformationJson, Link newLink) {
        Long id;
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        newLink.setId(businessWithNextGeneralKey.getNextGeneralId());
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(newLink.getPersonId(), currentUserInformationJson.userName,
                "Link:New:" + newLink.getId().toString(), "Day: " + businessWithLink.getDayNameFromHourId(newLink.getHourId())
                        + ", Hour: " + businessWithLink.getHourFromHourId(newLink.getHourId()),
                "Type: " + AdorationMethodTypes.getTranslatedString(newLink.getType())
                        + ", Priority: " + newLink.getPriority().toString());
        auditTrailCollection.add(auditTrail);
        id = businessWithLink.newLink(newLink, auditTrailCollection);
        return id;
    }

    private boolean isNewPersonValid(CurrentUserInformationJson currentUserInformationJson, Link oldLink, Link newLink) {
        boolean result = true;
        Person p = businessWithPerson.getPersonById(newLink.getPersonId());
        if (p == null) {
            logger.info("{} {} tried to create/update Link for a non-existing Person.", USER, currentUserInformationJson.userName);
            result = false;
        }
        if (!newLink.getPersonId().equals(oldLink.getPersonId())) {
            //changing person is not supported so this request must be rouge
            logger.info("{} {} tried to change Person for Link: {}", USER, currentUserInformationJson.userName, newLink.getId());
            result = false;
        }
        return result;
    }

    /**
     * Update a person commitment (technically a link).
     *
     * @param link                       to be updated
     * @param currentUserInformationJson is the actual user
     * @return with id of the updated link
     */
    public Long updatePersonCommitment(Link link, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long id;
        Link oldLink;
        link.setAdminComment(link.getAdminComment().trim());
        link.setPublicComment(link.getPublicComment().trim());
        if (link.getId() == 0) {
            //new Link
            return createPersonCommitment(currentUserInformationJson, link);
        } else { //fill old link
            oldLink = businessWithLink.getLink(link.getId());
            if (oldLink == null) {
                logger.info("{} {} tried to update a not existing Person Commitment/Link", USER, currentUserInformationJson.userName);
                throw new DatabaseHandlingException(CANNOT_UPDATE_PERSON_COMMITMENT_TEXT);
            }
        }
        //hourid
        handleHourIdUpdate(link, oldLink, currentUserInformationJson.userName, auditTrailCollection);
        //personId
        if (!isNewPersonValid(currentUserInformationJson, oldLink, link)) {
            //something is wrong with the person settings
            throw new DatabaseHandlingException(CANNOT_UPDATE_PERSON_COMMITMENT_TEXT);
        }
        //priority
        handlePriorityUpdate(link, oldLink, currentUserInformationJson.userName, auditTrailCollection);
        //admincomment
        String newValue = link.getAdminComment();
        String oldValue = oldLink.getAdminComment();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        if (!newValue.contentEquals(oldValue)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(link.getPersonId(), link.getId(), currentUserInformationJson.userName, "Admin Comment", oldValue, newValue));
        }
        //publicComment
        newValue = link.getPublicComment();
        oldValue = oldLink.getPublicComment();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        if (!newValue.contentEquals(oldValue)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(link.getPersonId(), link.getId(), currentUserInformationJson.userName, "Public Comment", oldValue, newValue));
        }
        //type
        handleTypeUpdate(link, oldLink, currentUserInformationJson.userName, auditTrailCollection);

        id = businessWithLink.updateLink(link, auditTrailCollection);
        return id;
    }

    private void handleTypeUpdate(Link newLink, Link oldLink, String userName, Collection<AuditTrail> auditTrailCollection) {
        Integer newInt = newLink.getType();
        Integer oldInt = oldLink.getType();
        if ((newInt < 0) || (newInt > 1)) {
            logger.info("{} {} tried to create/update Link with bad type.", USER, userName);
            throw new DatabaseHandlingException(CANNOT_UPDATE_PERSON_COMMITMENT_TEXT);
        }
        if (!newInt.equals(oldInt)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(newLink.getPersonId(), newLink.getId(), userName, "Type",
                    AdorationMethodTypes.getTranslatedString(oldInt), AdorationMethodTypes.getTranslatedString(newInt)));
        }
    }

    private void handlePriorityUpdate(Link newLink, Link oldLink, String userName, Collection<AuditTrail> auditTrailCollection) {
        Integer newInt = newLink.getPriority();
        Integer oldInt = oldLink.getPriority();
        if ((newInt < 0) || (newInt > MAX_PRIORITY)) { //need to synch with applog.jsp
            logger.info("{} {} tried to create/update Link with bad priority.", USER, userName);
            throw new DatabaseHandlingException(CANNOT_UPDATE_PERSON_COMMITMENT_TEXT);
        }
        if (!newInt.equals(oldInt)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(newLink.getPersonId(), newLink.getId(), userName, "Priority", oldInt.toString(), newInt.toString()));
        }
    }

    private void handleHourIdUpdate(Link newLink, Link oldLink, String userName, Collection<AuditTrail> auditTrailCollection) {
        Integer newInt = newLink.getHourId();
        Integer oldInt = oldLink.getHourId();
        if (!businessWithLink.isValidHour(newInt)) {
            logger.info("{} {} tried to create/update Link with bad hour id: {}.", USER, userName, newInt);
            throw new DatabaseHandlingException(CANNOT_UPDATE_PERSON_COMMITMENT_TEXT);
        }
        if (!newInt.equals(oldInt)) {
            auditTrailCollection.add(prepareUpdateAuditTrail(newLink.getPersonId(), newLink.getId(), userName, "Day/Hour",
                    businessWithLink.getDayNameFromHourId(oldLink.getHourId()) + "/" + businessWithLink.getHourFromHourId(oldLink.getHourId()),
                    businessWithLink.getDayNameFromHourId(newLink.getHourId()) + "/" + businessWithLink.getHourFromHourId(newLink.getHourId())));
        }
    }

    /**
     * Delete an hour that is associated to an adorator.
     *
     * @param deleteEntityJson           is the id of the Link to be deleted
     * @param currentUserInformationJson is the actual user
     * @return with the id of the deleted Link
     */
    public Long deletePersonCommitment(DeleteEntityJson deleteEntityJson, CurrentUserInformationJson currentUserInformationJson) {
        Long id = Long.parseLong(deleteEntityJson.entityId);
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
