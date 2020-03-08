package org.rockhill.adorApp.web.provider;

import org.apache.commons.text.StringEscapeUtils;
import org.rockhill.adorApp.database.business.BusinessWithAuditTrail;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.helper.Converter;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.business.helper.enums.WebStatusTypes;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Component
public class PeopleProvider {

    private final Logger logger = LoggerFactory.getLogger(PeopleProvider.class);


    @Autowired
    BusinessWithPerson businessWithPerson;
    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;

    public Object getPersonListAsObject() {
        List<Person> people = businessWithPerson.getPersonList();
        return people;
    }

    public Object getPersonAsObject(final Long id) {
        Person p = businessWithPerson.getPersonById(id);
        return p;
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Person::Update", fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"");
        return auditTrail;
    }

    public Long updatePerson(PersonInformationJson p, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long id = Long.parseLong(p.id);
        Person person = businessWithPerson.getPersonById(id);
        if (person == null) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update not existing Person");
            return null;
        }
        //prepare new name and validate it
        String newValue = StringEscapeUtils.escapeHtml4(p.name.trim());
        String oldValue = person.getName();
        //name length must be > 0, and shall not fit to other existing names
        if (newValue.length() == 0) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Person with empty name.");
            return null;
        }
        person.setName(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "Name", oldValue, newValue));
        }

        Integer newStatus = Integer.parseInt(p.adorationStatus);
        Integer oldStatus = person.getAdorationStatus();
        person.setAdorationStatus(newStatus);
        if (!oldStatus.equals(newStatus)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "AdorationStatus",
                    AdoratorStatusTypes.getTranslatedString(oldStatus), AdoratorStatusTypes.getTranslatedString(newStatus)));
        }

        oldStatus = person.getWebStatus();
        newStatus = Integer.parseInt(p.webStatus);
        person.setWebStatus(newStatus);
        if (!oldStatus.equals(newStatus)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "WebStatus",
                    WebStatusTypes.getTranslatedString(oldStatus), WebStatusTypes.getTranslatedString(newStatus)));
        }

        oldValue = person.getMobile();
        newValue = StringEscapeUtils.escapeHtml4(p.mobile.trim());
        person.setMobile(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "Mobile", oldValue, newValue));
        }

        Boolean oldBoolean = person.getMobileVisible();
        Boolean newBoolean = p.mobileVisible.contains("on");
        person.setMobileVisible(newBoolean);
        if (oldBoolean != newBoolean) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "MobileVisible", oldBoolean.toString(), newBoolean.toString()));
        }

        oldValue = person.getEmail();
        newValue = StringEscapeUtils.escapeHtml4(p.email.trim());
        person.setEmail(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "Email", oldValue, newValue));
        }

        oldBoolean = person.getEmailVisible();
        newBoolean = p.emailVisible.contains("on");
        person.setEmailVisible(newBoolean);
        if (oldBoolean != newBoolean) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "EmailVisible", oldBoolean.toString(), newBoolean.toString()));
        }

        oldValue = person.getAdminComment();
        newValue = StringEscapeUtils.escapeHtml4(p.adminComment.trim());
        person.setAdminComment(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "AdminComment", oldValue, newValue));
        }

        oldBoolean = person.getDhcSigned();
        newBoolean = p.dhcSigned.contains("on");
        person.setDhcSigned(newBoolean);
        if (oldBoolean != newBoolean) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "DhcSigned", oldBoolean.toString(), newBoolean.toString()));
        }

        Date oldDate = person.getDhcSignedDate();
        Date newDate;
        try {
            DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            newDate = df1.parse(p.dhcSignedDate);
        } catch (ParseException e) {
            newDate = null;
        }
        person.setDhcSignedDate(newDate);
        Converter converter = new Converter();
        newValue = converter.getDateAsString(newDate);
        oldValue = converter.getDateAsString(oldDate);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "DhcSignedDate", oldValue, newValue));
        }

        oldValue = person.getCoordinatorComment();
        newValue = StringEscapeUtils.escapeHtml4(p.coordinatorComment.trim());
        person.setCoordinatorComment(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "CoordinatorComment", oldValue, newValue));
        }

        oldValue = person.getVisibleComment();
        newValue = StringEscapeUtils.escapeHtml4(p.visibleComment.trim());
        person.setVisibleComment(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "VisibleComment", oldValue, newValue));
        }

        //+ we do not set String languageCode;
        id = businessWithPerson.updatePerson(person, auditTrailCollection);
        return id;
    }
}
