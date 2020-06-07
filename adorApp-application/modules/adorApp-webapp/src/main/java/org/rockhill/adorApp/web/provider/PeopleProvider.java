package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithAuditTrail;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.business.helper.enums.WebStatusTypes;
import org.rockhill.adorApp.database.exception.DatabaseHandlingException;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    protected void checkDangerousValue(final String text, final String userName) {
        Pattern p = Pattern.compile("[\\\\#&\\<\\>]");
        Matcher m = p.matcher(text);
        if (m.find()) {
            logger.warn("User:" + userName + " tried to use dangerous string value:" + text);
            throw new DatabaseHandlingException("Field content is not allowed.");
        }
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
        String newValue = p.name.trim();
        checkDangerousValue(newValue, currentUserInformationJson.userName);
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
        newValue = p.mobile.trim();
        checkDangerousValue(newValue, currentUserInformationJson.userName);
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
        newValue = p.email.trim();
        checkDangerousValue(newValue, currentUserInformationJson.userName);
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
        newValue = p.adminComment.trim();
        checkDangerousValue(newValue, currentUserInformationJson.userName);
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

        oldValue = person.getDhcSignedDate();
        newValue = p.dhcSignedDate;
        checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setDhcSignedDate(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "DhcSignedDate", oldValue, newValue));
        }

        oldValue = person.getCoordinatorComment();
        newValue = p.coordinatorComment.trim();
        checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setCoordinatorComment(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "CoordinatorComment", oldValue, newValue));
        }

        oldValue = person.getVisibleComment();
        newValue = p.visibleComment.trim();
        checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setVisibleComment(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "VisibleComment", oldValue, newValue));
        }

        //+ we do not set String languageCode;
        id = businessWithPerson.updatePerson(person, auditTrailCollection);
        return id;
    }

    public Object getPersonHistoryAsObject(Long id) {
        List<AuditTrail> a = businessWithAuditTrail.getAuditTrailOfObject(id);
        return a;
    }
}
