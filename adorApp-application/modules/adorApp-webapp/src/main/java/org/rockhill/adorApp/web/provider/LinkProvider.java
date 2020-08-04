package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.*;
import org.rockhill.adorApp.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.LinkJson;
import org.rockhill.adorApp.web.json.PersonJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class LinkProvider {

    private final Logger logger = LoggerFactory.getLogger(LinkProvider.class);
    private final String subject = "[AdoratorApp] - Új óra";

    @Autowired
    BusinessWithPerson businessWithPerson;
    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    BusinessWithLink businessWithLink;
    @Autowired
    BusinessWithTranslator businessWithTranslator;

    public Object getLinkListAsObject(CurrentUserInformationJson currentUserInformationJson) {
        LinkJson linkJson = new LinkJson();
        List<Link> linkList = businessWithLink.getLinkList();
        linkJson.linkList = linkList;
        Set<Long> personIds = new HashSet<>();
        if (linkJson.linkList != null) {
            for (Link l: linkJson.linkList) {
                personIds.add(l.getPersonId());
            }
        }
        Iterator<Long> ppl = personIds.iterator();
        List<PersonJson> relatedPersonList = new LinkedList<>();
        while (ppl.hasNext()) {
            Long id = ppl.next();
            Person p = businessWithPerson.getPersonById(id);
            if (p != null) {
                relatedPersonList.add(new PersonJson(p,currentUserInformationJson.isPrivilegedUser()));
            } else {
                logger.warn("Person ID usage found without real Person, id: " + id.toString());
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

    public Object getLinkHistoryAsObject(Long id) {
        List<AuditTrail> a = businessWithAuditTrail.getAuditTrailOfObject(id);
        return a;
    }

    public Object getLinkAsObject(Long id, CurrentUserInformationJson currentUserInformationJson) {
        LinkJson linkJson = new LinkJson();
        Link link = businessWithLink.getLink(id);
        List<Link> linkList = new LinkedList<>();
        linkList.add(link);
        linkJson.linkList = linkList;
        List<PersonJson> relatedPersonList = new LinkedList<>();
        Person p = businessWithPerson.getPersonById(link.getPersonId());
        if (p != null) {
            relatedPersonList.add(new PersonJson(p,currentUserInformationJson.isPrivilegedUser()));
        } else {
            logger.warn("Person ID usage found without real Person, id: " + id.toString());
        }
        linkJson.relatedPersonList = relatedPersonList;
        return linkJson;
    }
}
