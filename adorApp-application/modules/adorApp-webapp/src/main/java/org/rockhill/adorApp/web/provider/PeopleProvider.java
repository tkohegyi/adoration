package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.*;
import org.rockhill.adorApp.database.business.helper.Converter;
import org.rockhill.adorApp.database.business.helper.enums.AdorationMethodTypes;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.exception.DatabaseHandlingException;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;
import org.rockhill.adorApp.helper.EmailSender;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.rockhill.adorApp.web.json.RegisterAdoratorJson;
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
    private final String subject = "[AdoratorApp] - Új adoráló";

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
    @Autowired
    EmailSender emailSender;


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
            person.setDhcSigned(Boolean.getBoolean(p.dhcSigned));
            person.setDhcSignedDate(p.dhcSignedDate);
            person.setEmail(p.email);
            person.setEmailVisible(Boolean.getBoolean(p.emailVisible));
            person.setLanguageCode("hu");
            person.setMobile(p.mobile);
            person.setMobileVisible(Boolean.getBoolean(p.mobileVisible));
            person.setVisibleComment(p.visibleComment);
            person.setIsAnonymous(Boolean.getBoolean(p.isAnonymous));
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

        Boolean oldBoolean = person.getIsAnonymous();
        Boolean newBoolean = p.isAnonymous.contains("true");
        person.setIsAnonymous(newBoolean);
        if (oldBoolean != newBoolean) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "isAnonymous", oldBoolean.toString(), newBoolean.toString()));
        }

        oldValue = person.getMobile();
        newValue = p.mobile.trim();
        businessWithAuditTrail.checkDangerousValue(newValue, currentUserInformationJson.userName);
        person.setMobile(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), currentUserInformationJson.userName, "Mobile", oldValue, newValue));
        }

        oldBoolean = person.getMobileVisible();
        newBoolean = p.mobileVisible.contains("true");
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

    public Long registerAdorator(RegisterAdoratorJson p, CurrentUserInformationJson currentUserInformationJson) {
        //validations
        if (p.name == null || p.comment == null || p.email == null || p.coordinate == null || p.dhc == null || p.mobile == null) {
            logger.warn("User:" + currentUserInformationJson.userName + " tried to use null value(s) for a new Adorator.");
            throw new DatabaseHandlingException("Field content (nullString) is not allowed.");
        }
        businessWithAuditTrail.checkDangerousValue(p.name, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(p.comment, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(p.email, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(p.coordinate, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(p.dhc, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(p.mobile, currentUserInformationJson.userName);
        if (p.dayId == null || p.hourId == null || p.dayId < 0 || p.dayId > 6 || p.hourId < 0 || p.hourId > 23
        || p.method < 1 || p.method > 3) {
            logger.warn("User:" + currentUserInformationJson.userName + "/" + p.name + " tried to use dangerous value for a new Adorator.");
            throw new DatabaseHandlingException("Field content (Integer) is not allowed.");
        }
        //if person is identified, then it is not a new adorator
        if (p.personId != null) {
            logger.warn("User:" + currentUserInformationJson.userName + "/" + p.name + " tried to register again.");
            throw new DatabaseHandlingException("Duplicated registration is not allowed.");
        }
        if (p.dhc == null || !p.dhc.contentEquals("consent-yes")) {
            logger.warn("User:" + currentUserInformationJson.userName + "/" + p.name + " tried to register without consent.");
            throw new DatabaseHandlingException("Data handling consent is missing.");
        }
        Long newId = businessWithNextGeneralKey.getNextGeneralId();
        Converter converter = new Converter();
        String dhcSignedDate = converter.getCurrentDateAsString();
        p.dhcSignedDate = dhcSignedDate;
        //send mail about the person
        String text = "New id: " + newId + "\nDHC Signed Date: " + dhcSignedDate + "\nAdatok:\n" + p.toString();
        emailSender.sendMail(subject, text);
        //new Person
        Person person = new Person();
        person.setId(newId);
        person.setName(p.name);
        person.setAdorationStatus(AdoratorStatusTypes.PRE_ADORATOR.getAdoratorStatusValue());
        person.setAdminComment("Adorálás módja: " + p.method + ", Segítség:" + p.coordinate + ", DHC:" + p.dhc + ", SelfComment: " + p.comment );
        person.setDhcSigned(true);
        person.setDhcSignedDate(dhcSignedDate);
        person.setEmail(p.email);
        person.setEmailVisible(true);
        person.setLanguageCode("hu");
        person.setMobile(p.mobile);
        person.setMobileVisible(true);
        person.setVisibleComment("");
        person.setIsAnonymous(false);
        person.setCoordinatorComment("");
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(person.getId(), "SYSTEM",
                "Person:New:" + person.getId(), "Új adoráló regisztrációja.", text);
        Long id = businessWithPerson.newPerson(person, auditTrail);
        return id;
    }
}
