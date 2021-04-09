package org.rockhill.adoration.web.provider;

import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.database.business.BusinessWithLink;
import org.rockhill.adoration.database.business.BusinessWithPerson;
import org.rockhill.adoration.database.business.BusinessWithTranslator;
import org.rockhill.adoration.database.business.helper.DateTimeConverter;
import org.rockhill.adoration.database.business.helper.enums.AdorationMethodTypes;
import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.DeleteEntityJson;
import org.rockhill.adoration.web.json.LinkJson;
import org.rockhill.adoration.web.json.PersonCommitmentJson;
import org.rockhill.adoration.web.json.PersonJson;
import org.rockhill.adoration.web.provider.helper.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Class to provide information about Links related information and update possibilities.
 */
@Component
public class LinkProvider extends ProviderBase {

    private final Logger logger = LoggerFactory.getLogger(LinkProvider.class);

    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithLink businessWithLink;
    @Autowired
    private BusinessWithTranslator businessWithTranslator;
    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private CoverageProvider coverageProvider;

    /**
     * Get full link list.
     *
     * @return with the info in json object form
     */
    public Object getLinkListAsObject(CurrentUserInformationJson currentUserInformationJson) {
        LinkJson linkJson = new LinkJson();
        List<Link> linkList;
        linkList = businessWithLink.getLinkList();
        linkJson.linkList = linkList;
        Set<Long> personIds = new HashSet<>();
        if (linkJson.linkList != null) {
            for (Link l : linkJson.linkList) {
                personIds.add(l.getPersonId());
            }
        }
        Iterator<Long> ppl = personIds.iterator();
        List<PersonJson> relatedPersonList = new LinkedList<>();
        while (ppl.hasNext()) {
            Long id = ppl.next();
            Person p = businessWithPerson.getPersonById(id);
            if (p != null) {
                relatedPersonList.add(new PersonJson(p, currentUserInformationJson.isPrivilegedUser()));
            } else {
                logger.warn("Person ID usage found without real Person, id: {}", id);
            }
        }
        linkJson.relatedPersonList = relatedPersonList;
        //fill the day names first
        linkJson.dayNames = new HashMap<>();
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            String textId = dayName.toString();
            String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
            linkJson.dayNames.put(dayName.getDayValue(), value);
        }
        return linkJson;
    }

    /**
     * Get info about a specific link.
     *
     * @return with the info in json object form
     */
    public Object getLinkAsObject(Long id, CurrentUserInformationJson currentUserInformationJson) {
        LinkJson linkJson = new LinkJson();
        Link link = businessWithLink.getLink(id);
        List<Link> linkList = new LinkedList<>();
        linkList.add(link);
        linkJson.linkList = linkList;
        List<PersonJson> relatedPersonList = new LinkedList<>();
        Person p = businessWithPerson.getPersonById(link.getPersonId());
        if (p != null) {
            relatedPersonList.add(new PersonJson(p, currentUserInformationJson.isPrivilegedUser()));
        } else {
            logger.warn("Person ID usage found without real Person, id: {}", id);
        }
        linkJson.relatedPersonList = relatedPersonList;
        return linkJson;
    }

    /**
     * Get audit history for a specified Link.
     *
     * @return with the info in json object form
     */
    public Object getLinkHistoryAsObject(Long id) {
        return getEntityHistoryAsObject(businessWithAuditTrail, id);
    }

    /**
     * Register a new Link to cover one-time registration.
     *
     * @param p is the hour to be covered
     * @param currentUserInformationJson is the person who are registering the hour
     * @return with id of the created Link or null in case of error
     */
    public Long registerOneTimeAdoration(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        Link link = new Link();
        Integer hourId;
        try {
            hourId = Integer.parseInt(p.entityId);
        } catch (NumberFormatException e) {
            throw new DatabaseHandlingException("Specified Hour is invalid.");
        }
        link.setId(0L); //enforced new link creation
        link.setHourId(hourId);
        link.setPriority(1);
        link.setPersonId(currentUserInformationJson.personId);
        link.setType(AdorationMethodTypes.ONETIME_ON.getAdorationMethodValue());
        link.setAdminComment(calculateTargetDate(hourId));
        return coverageProvider.updatePersonCommitment(link, currentUserInformationJson);
    }

    /**
     * Register a new Link to cover one-time miss.
     *
     * @param p is the hour to be missed
     * @param currentUserInformationJson is the person who are registering the hour
     * @return with id of the created Link or null in case of error
     */
    public Long registerOneTimeMiss(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        Link link = new Link();
        Integer hourId;
        try {
            hourId = Integer.parseInt(p.entityId);
        } catch (NumberFormatException e) {
            throw new DatabaseHandlingException("Specified Hour is invalid.");
        }
        //check that the person has hour to miss :)
        if (!currentUserInformationJson.isAdoratorAdmin && !coverageProvider.isPersonCommittedToHour(currentUserInformationJson.personId, hourId)) {
            //well, nothing to miss
            throw new DatabaseHandlingException("Specified Hour is not owned.");
        }
        link.setId(0L); //enforced new link creation
        link.setHourId(hourId);
        link.setPriority(1);
        link.setPersonId(currentUserInformationJson.personId);
        link.setType(AdorationMethodTypes.ONETIME_OFF.getAdorationMethodValue());
        link.setAdminComment(calculateTargetDate(hourId));
        return coverageProvider.updatePersonCommitment(link, currentUserInformationJson);
    }

    private String calculateTargetDate(Integer hourId) {
        //figure out closest date of specified hour
        Calendar cal = Calendar.getInstance();
        int hourIdNow = (cal.get(Calendar.DAY_OF_WEEK) - 1) * BusinessWithLink.HOUR_IN_A_DAY + cal.get(Calendar.HOUR_OF_DAY);  // use sun as 0 day
        int diff = hourId - hourIdNow;
        if (diff < 0) {
            diff+= Link.MAX_HOUR + 1;
        }
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        Date targetDate = new Date();
        targetDate.setTime(targetDate.getTime() + diff * DateTimeConverter.HOUR_IN_MS);
        return dateTimeConverter.getDateAsString(targetDate);
    }

    public Long unRegisterOneTimeAdoration(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        return coverageProvider.deletePersonCommitment(p, currentUserInformationJson);
    }

    public Long unRegisterOneTimeMiss(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        return coverageProvider.deletePersonCommitment(p, currentUserInformationJson);
    }
}
