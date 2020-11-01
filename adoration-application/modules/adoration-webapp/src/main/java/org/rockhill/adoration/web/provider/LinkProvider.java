package org.rockhill.adoration.web.provider;

import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.database.business.BusinessWithLink;
import org.rockhill.adoration.database.business.BusinessWithPerson;
import org.rockhill.adoration.database.business.BusinessWithTranslator;
import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.LinkJson;
import org.rockhill.adoration.web.json.PersonJson;
import org.rockhill.adoration.web.provider.helper.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
}
