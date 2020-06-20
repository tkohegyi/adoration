package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.*;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.business.helper.enums.WebStatusTypes;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class PeopleProvider {

    private final Logger logger = LoggerFactory.getLogger(PeopleProvider.class);


    @Autowired
    BusinessWithPerson businessWithPerson;
    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    BusinessWithLink businessWithLink;
    @Autowired
    BusinessWithSocial businessWithSocial;


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
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Person:Update:" + id.toString(), fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    public Long updatePerson(PersonInformationJson p, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long id = Long.parseLong(p.id);
        Person person = businessWithPerson.getPersonById(id);
        if (person == null) {
            //new Person
            person = new Person();
            person.setId(businessWithNextGeneralKey.getNextGeneralId());
            person.setName(p.name);
            person.setAdorationStatus(Integer.parseInt(p.adorationStatus));
            person.setAdminComment(p.adminComment);
            person.setCoordinatorComment(p.coordinatorComment);
            person.setDhcSigned(new Boolean(p.dhcSigned));
            person.setDhcSignedDate(p.dhcSignedDate);
            person.setEmail(p.email);
            person.setEmailVisible(new Boolean(p.emailVisible));
            person.setLanguageCode("hu");
            person.setMobile(p.mobile);
            person.setMobileVisible(new Boolean(p.mobileVisible));
            person.setVisibleComment(p.visibleComment);
            person.setWebStatus(Integer.parseInt(p.webStatus));
            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(person.getId(), currentUserInformationJson.userName,
                    "Person:New:" + person.getId(), "Name: " + person.getName() + ", e-mail: " + person.getEmail()
                            + ", Phone: " + person.getMobile(), "");
            id = businessWithPerson.newPerson(person, auditTrail);
            return id;
        }
        //prepare new name and validate it
        String newValue = p.name.trim();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
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
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setMobile(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "Mobile", oldValue, newValue));
        }

        Boolean oldBoolean = person.getMobileVisible();
        Boolean newBoolean = p.mobileVisible.contains("true");
        person.setMobileVisible(newBoolean);
        if (oldBoolean != newBoolean) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "MobileVisible", oldBoolean.toString(), newBoolean.toString()));
        }

        oldValue = person.getEmail();
        newValue = p.email.trim();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setEmail(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "Email", oldValue, newValue));
        }

        oldBoolean = person.getEmailVisible();
        newBoolean = p.emailVisible.contains("true");
        person.setEmailVisible(newBoolean);
        if (oldBoolean != newBoolean) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "EmailVisible", oldBoolean.toString(), newBoolean.toString()));
        }

        oldValue = person.getAdminComment();
        newValue = p.adminComment.trim();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setAdminComment(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "AdminComment", oldValue, newValue));
        }

        oldBoolean = person.getDhcSigned();
        newBoolean = p.dhcSigned.contains("true");
        person.setDhcSigned(newBoolean);
        if (oldBoolean != newBoolean) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "DhcSigned", oldBoolean.toString(), newBoolean.toString()));
        }

        oldValue = person.getDhcSignedDate();
        newValue = p.dhcSignedDate;
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setDhcSignedDate(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "DhcSignedDate", oldValue, newValue));
        }

        oldValue = person.getCoordinatorComment();
        newValue = p.coordinatorComment.trim();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setCoordinatorComment(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "CoordinatorComment", oldValue, newValue));
        }

        oldValue = person.getVisibleComment();
        newValue = p.visibleComment.trim();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
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

    public Long deletePerson(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        Long personId = Long.parseLong(p.entityId);
        Person person = businessWithPerson.getPersonById(personId);
        //collect related social - this can be null, if there was no social for the person + we need to clear the social - person connection only
        List<Social> socialList = businessWithSocial.getSocialsOfPerson(person);
        //collect related links
        List<Link> linkList = businessWithLink.getLinksOfPerson(person);
        //collect related audit records
        List<AuditTrail> auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(personId);
        Long result = businessWithPerson.deletePerson(person, socialList, linkList, auditTrailList);
        return result;
    }
}
