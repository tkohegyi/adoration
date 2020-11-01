package org.rockhill.adoration.web.provider;

import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.database.business.BusinessWithLink;
import org.rockhill.adoration.database.business.BusinessWithNextGeneralKey;
import org.rockhill.adoration.database.business.BusinessWithPerson;
import org.rockhill.adoration.database.business.BusinessWithSocial;
import org.rockhill.adoration.database.business.BusinessWithTranslator;
import org.rockhill.adoration.database.business.helper.DateTimeConverter;
import org.rockhill.adoration.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adoration.database.business.helper.enums.TranslatorDayNames;
import org.rockhill.adoration.database.exception.DatabaseHandlingException;
import org.rockhill.adoration.database.tables.AuditTrail;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;
import org.rockhill.adoration.helper.EmailSender;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.DeleteEntityJson;
import org.rockhill.adoration.web.json.LinkJson;
import org.rockhill.adoration.web.json.MessageToCoordinatorJson;
import org.rockhill.adoration.web.json.PersonInformationJson;
import org.rockhill.adoration.web.json.PersonJson;
import org.rockhill.adoration.web.json.RegisterAdoratorJson;
import org.rockhill.adoration.web.provider.helper.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Class to provide information about users.
 */
@Component
public class PeopleProvider extends ProviderBase {

    private static final String USER = "User:";
    private static final String SUBJECT_NEW_ADORATOR = "[AdoratorApp] - Új adoráló";
    private static final String SUBJECT_NEW_MESSAGE = "[AdoratorApp] - Üzenet egy felhasználótól";

    private final Logger logger = LoggerFactory.getLogger(PeopleProvider.class);

    @Autowired
    private BusinessWithPerson businessWithPerson;
    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    private BusinessWithLink businessWithLink;
    @Autowired
    private BusinessWithSocial businessWithSocial;
    @Autowired
    private BusinessWithTranslator businessWithTranslator;
    @Autowired
    private EmailSender emailSender;


    /**
     * Get simple full list of people.
     *
     * @return with the list as object
     */
    public Object getPersonListAsObject() {
        List<Person> people;
        people = businessWithPerson.getPersonList();
        return people;
    }

    /**
     * Get a specific person.
     *
     * @param id of the person
     * @return with the person as object
     */
    public Object getPersonAsObject(final Long id) {
        Person person;
        person = businessWithPerson.getPersonById(id);
        return person;
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Person:Update:" + id.toString(),
                fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    /**
     * Update a Person information.
     *
     * @param personInformationJson      is the updated Person information to be saved
     * @param currentUserInformationJson is the actual user
     * @return with the id of the updated Person
     */
    public Long updatePerson(PersonInformationJson personInformationJson, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long id = Long.parseLong(personInformationJson.id);
        Person person = businessWithPerson.getPersonById(id);
        if (person == null) {
            //new Person
            id = createNewPerson(personInformationJson, currentUserInformationJson.userName);
            return id;
        }
        //prepare new name and validate it
        handleNameUpdate(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);
        //adorationStatus
        handleAdorationStatusUpdate(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);
        //other string fields
        handleAllOtherStringFields(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);
        //other boolean fields
        handleAllOtherBooleanFields(personInformationJson, person, currentUserInformationJson.userName, auditTrailCollection);

        //we do not set the string languageCode
        id = businessWithPerson.updatePerson(person, auditTrailCollection);
        return id;
    }

    private void handleAllOtherBooleanFields(PersonInformationJson personInformationJson, Person person, String userName, Collection<AuditTrail> auditTrailCollection) {
        Boolean newBoolean;
        //isAnonymous
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.isAnonymous.contains("true"), person.getIsAnonymous(),
                userName, auditTrailCollection, "isAnonymous");
        person.setIsAnonymous(newBoolean);
        //mobileVisible
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.mobileVisible.contains("true"), person.getMobileVisible(),
                userName, auditTrailCollection, "MobileVisible");
        person.setMobileVisible(newBoolean);
        //emailVisible
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.emailVisible.contains("true"), person.getEmailVisible(),
                userName, auditTrailCollection, "EmailVisible");
        person.setEmailVisible(newBoolean);
        //dchSigned
        newBoolean = handleSimpleBooleanFieldUpdate(person.getId(), personInformationJson.dhcSigned.contains("true"), person.getDhcSigned(),
                userName, auditTrailCollection, "DhcSigned");
        person.setDhcSigned(newBoolean);
    }

    private void handleAllOtherStringFields(PersonInformationJson personInformationJson, Person person, String userName, Collection<AuditTrail> auditTrailCollection) {
        String newValue;
        //mobile
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.mobile.trim(), person.getMobile(),
                userName, auditTrailCollection, "Mobile");
        person.setMobile(newValue);
        //email
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.email.trim(), person.getEmail(),
                userName, auditTrailCollection, "Email");
        person.setEmail(newValue);
        //adminComment
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.adminComment.trim(), person.getAdminComment(),
                userName, auditTrailCollection, "AdminComment");
        person.setAdminComment(newValue);
        //dhcSignedDate
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.dhcSignedDate, person.getDhcSignedDate(),
                userName, auditTrailCollection, "DhcSignedDate");
        person.setDhcSignedDate(newValue);
        //coordinatorComment
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.coordinatorComment.trim(), person.getCoordinatorComment(),
                userName, auditTrailCollection, "CoordinatorComment");
        person.setCoordinatorComment(newValue);
        //visibleComment
        newValue = handleSimpleStringFieldUpdate(person.getId(), personInformationJson.visibleComment.trim(), person.getVisibleComment(),
                userName, auditTrailCollection, "VisibleComment");
        person.setVisibleComment(newValue);
    }

    private void handleAdorationStatusUpdate(PersonInformationJson personInformationJson, Person person, String userName, Collection<AuditTrail> auditTrailCollection) {
        Integer newStatus = Integer.parseInt(personInformationJson.adorationStatus);
        Integer oldStatus = person.getAdorationStatus();
        person.setAdorationStatus(newStatus);
        if (!oldStatus.equals(newStatus)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), userName, "AdorationStatus",
                    AdoratorStatusTypes.getTranslatedString(oldStatus), AdoratorStatusTypes.getTranslatedString(newStatus)));
        }
    }

    private Boolean handleSimpleBooleanFieldUpdate(Long refId, Boolean newBoolean, Boolean oldBoolean,
                                                   String userName, Collection<AuditTrail> auditTrailCollection, String fieldName) {
        if (!oldBoolean.equals(newBoolean)) {
            auditTrailCollection.add(prepareAuditTrail(refId, userName, fieldName, oldBoolean.toString(), newBoolean.toString()));
        }
        return newBoolean;
    }

    private String handleSimpleStringFieldUpdate(Long refId, String newValue, String oldValue,
                                                 String userName, Collection<AuditTrail> auditTrailCollection, String fieldName) {
        businessWithAuditTrail.checkDangerousValue(newValue, userName);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(refId, userName, fieldName, oldValue, newValue));
        }
        return newValue;
    }

    private void handleNameUpdate(PersonInformationJson personInformationJson, Person person,
                                  String userName, Collection<AuditTrail> auditTrailCollection) {
        String newValue = personInformationJson.name.trim();
        String oldValue = person.getName();
        businessWithAuditTrail.checkDangerousValue(newValue, userName);
        //name length must be > 0, and shall not fit to other existing names
        if (newValue.length() == 0) {
            logger.info("{} {} tried to create/update Person with empty name.", USER, userName);
            throw new DatabaseHandlingException("Field content is not allowed.");
        }
        person.setName(newValue);
        if (!oldValue.contentEquals(newValue)) {
            auditTrailCollection.add(prepareAuditTrail(person.getId(), userName, "Name", oldValue, newValue));
        }
    }

    private Long createNewPerson(PersonInformationJson personInformationJson, String userName) {
        Long id;
        Person person = new Person();
        person.setId(businessWithNextGeneralKey.getNextGeneralId());
        person.setName(personInformationJson.name);
        person.setAdorationStatus(Integer.parseInt(personInformationJson.adorationStatus));
        person.setAdminComment(personInformationJson.adminComment);
        person.setCoordinatorComment(personInformationJson.coordinatorComment);
        person.setDhcSigned(Boolean.getBoolean(personInformationJson.dhcSigned));
        person.setDhcSignedDate(personInformationJson.dhcSignedDate);
        person.setEmail(personInformationJson.email);
        person.setEmailVisible(Boolean.getBoolean(personInformationJson.emailVisible));
        person.setLanguageCode("hu");
        person.setMobile(personInformationJson.mobile);
        person.setMobileVisible(Boolean.getBoolean(personInformationJson.mobileVisible));
        person.setVisibleComment(personInformationJson.visibleComment);
        person.setIsAnonymous(Boolean.getBoolean(personInformationJson.isAnonymous));
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(person.getId(), userName,
                "Person:New:" + person.getId(), "Name: " + person.getName() + ", e-mail: " + person.getEmail()
                        + ", Phone: " + person.getMobile(), "");
        id = businessWithPerson.newPerson(person, auditTrail);
        return id;
    }

    /**
     * Get the audit records of the Person.
     *
     * @param id is the identifier of the Person
     * @return with the list of audit events as object
     */
    public Object getPersonHistoryAsObject(Long id) {
        return getEntityHistoryAsObject(businessWithAuditTrail, id);
    }

    /**
     * Delete a specific Person.
     *
     * @param personJson identifies the Person
     * @return with the id of the deleted Person
     */
    public Long deletePerson(DeleteEntityJson personJson) {
        Long personId = Long.parseLong(personJson.entityId);
        Person person = businessWithPerson.getPersonById(personId);
        //collect related social - this can be null, if there was no social for the person + we need to clear the social - person connection only
        List<Social> socialList = businessWithSocial.getSocialsOfPerson(person);
        //collect related links
        List<Link> linkList = businessWithLink.getLinksOfPerson(person);
        //collect related audit records
        List<AuditTrail> auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(personId);
        Long result;
        result = businessWithPerson.deletePerson(person, socialList, linkList, auditTrailList);
        return result;
    }

    /**
     * Register a new adorator (add a new Person).
     *
     * @param adoratorJson               is the person details
     * @param currentUserInformationJson is the actual user
     * @return with the id of the registered adorator / Person
     */
    public Long registerAdorator(RegisterAdoratorJson adoratorJson, CurrentUserInformationJson currentUserInformationJson) {
        //validations
        if (adoratorJson.name == null || adoratorJson.comment == null || adoratorJson.email == null
                || adoratorJson.coordinate == null || adoratorJson.dhc == null || adoratorJson.mobile == null) {
            logger.warn("{} {} tried to use null value(s) for a new Adorator.", USER, currentUserInformationJson.userName);
            throw new DatabaseHandlingException("Field content (nullString) is not allowed.");
        }
        businessWithAuditTrail.checkDangerousValue(adoratorJson.name, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.comment, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.email, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.coordinate, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.dhc, currentUserInformationJson.userName);
        businessWithAuditTrail.checkDangerousValue(adoratorJson.mobile, currentUserInformationJson.userName);
        if (adoratorJson.dayId == null || adoratorJson.hourId == null || adoratorJson.dayId < 0 || adoratorJson.dayId > 6 || adoratorJson.hourId < 0 || adoratorJson.hourId > 23
                || adoratorJson.method < 1 || adoratorJson.method > 3) {
            logger.warn("{} {} / {} tried to use dangerous value for a new Adorator.", USER, currentUserInformationJson.userName, adoratorJson.name);
            throw new DatabaseHandlingException("Field content (Integer) is not allowed.");
        }
        //if person is identified, then it is not a new adorator
        if (adoratorJson.personId != null) {
            logger.warn("{} {} / {} tried to register again.", USER, currentUserInformationJson.userName, adoratorJson.name);
            throw new DatabaseHandlingException("Duplicated registration is not allowed.");
        }
        if (adoratorJson.dhc == null || !adoratorJson.dhc.contentEquals("consent-yes")) {
            logger.warn("{} {} / {}  tried to register without consent.", USER, currentUserInformationJson.userName, adoratorJson.name);
            throw new DatabaseHandlingException("Data handling consent is missing.");
        }
        Long id;
        id = createNewAdorator(adoratorJson);
        return id;
    }

    private Long createNewAdorator(RegisterAdoratorJson adoratorJson) {
        Long newId = businessWithNextGeneralKey.getNextGeneralId();
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        String dhcSignedDate = dateTimeConverter.getCurrentDateAsString();
        adoratorJson.dhcSignedDate = dhcSignedDate;
        //send mail about the person
        String text = "New id: " + newId + "\nDHC Signed Date: " + dhcSignedDate + "\nAdatok:\n" + adoratorJson.toString();
        emailSender.sendMailToAdministrator(SUBJECT_NEW_ADORATOR, text);
        //new Person
        Person person = new Person();
        person.setId(newId);
        person.setName(adoratorJson.name);
        person.setAdorationStatus(AdoratorStatusTypes.PRE_ADORATOR.getAdoratorStatusValue());
        person.setAdminComment("Adorálás módja: " + adoratorJson.method + ", Segítség:" + adoratorJson.coordinate
                + ", DHC:" + adoratorJson.dhc + ", SelfComment: " + adoratorJson.comment);
        person.setDhcSigned(true);
        person.setDhcSignedDate(dhcSignedDate);
        person.setEmail(adoratorJson.email);
        person.setEmailVisible(true);
        person.setLanguageCode("hu");
        person.setMobile(adoratorJson.mobile);
        person.setMobileVisible(true);
        person.setVisibleComment("");
        person.setIsAnonymous(false);
        person.setCoordinatorComment("");
        AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(person.getId(), "SYSTEM",
                "Person:New:" + person.getId(), "Új adoráló regisztrációja.", text);
        Long id;
        id = businessWithPerson.newPerson(person, auditTrail);
        return id;
    }

    /**
     * Gets the list of the adorators (People).
     *
     * @param currentUserInformationJson is the actual user
     * @param privilegedAdorator         if the actual user is privileged or not - privileged user has right to see hidden fields.
     * @return with the list of adorators as object
     */
    public Object getAdoratorListAsObject(CurrentUserInformationJson currentUserInformationJson, Boolean privilegedAdorator) {
        List<Person> people = businessWithPerson.getPersonList();
        List<PersonJson> personList = new LinkedList<>();
        List<Link> linkList = new LinkedList<>();
        //filter out ppl and fields
        for (Person p : people) {
            if (!AdoratorStatusTypes.isInactive(p.getAdorationStatus())) {
                personList.add(new PersonJson(p, privilegedAdorator));
                List<Link> personLinkList = businessWithLink.getLinksOfPerson(p);
                fillLinkListFromPersonLinkList(linkList, personLinkList);
            }
        }
        //now fill the structure
        LinkJson linkJson = new LinkJson();
        linkJson.linkList = linkList;
        linkJson.relatedPersonList = personList;
        //fill the day names
        linkJson.dayNames = new HashMap<>();
        for (TranslatorDayNames dayName : TranslatorDayNames.values()) {
            String textId = dayName.toString();
            String value = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode, textId, textId);
            linkJson.dayNames.put(dayName.getDayValue(), value);
        }
        return linkJson;
    }

    private void fillLinkListFromPersonLinkList(List<Link> linkList, List<Link> personLinkList) {
        if (personLinkList != null) {
            for (Link l : personLinkList) {
                if (!linkList.contains(l)) {
                    l.setAdminComment(""); //empty the admin comment part, since adorators shall not see this part
                    linkList.add(l);
                }
            }
        }
    }

    /**
     * Send Email message to the main coordinator/administrator.
     *
     * @param messageToCoordinatorJson   is the message descriptor json
     * @param currentUserInformationJson is the actual user
     */
    public void messageToCoordinator(MessageToCoordinatorJson messageToCoordinatorJson, CurrentUserInformationJson currentUserInformationJson) {
        String unknown = "[ Unknown ]";
        String socialText = currentUserInformationJson.socialServiceUsed == null ? unknown : currentUserInformationJson.socialServiceUsed;
        String socialId = currentUserInformationJson.socialId == null ? unknown : currentUserInformationJson.socialId.toString();
        String personId = currentUserInformationJson.personId == null ? unknown : currentUserInformationJson.personId.toString();
        String info = messageToCoordinatorJson.info == null ? "[ Nincs adat ]" : messageToCoordinatorJson.info;
        String message = messageToCoordinatorJson.text == null ? "[ Nincs üzenet ]" : messageToCoordinatorJson.text;
        //send mail from the person
        String text = "Felhasználó neve: " + currentUserInformationJson.loggedInUserName
                + "\n  Egyéb azonosító: \n   Bejelentkezés: " + socialText + "\n   Social ID: " + socialId + "\n   Person ID: " + personId
                + "\n\n  Kapcsolat üzenet: \n" + info
                + "\n\n  Üzenet:\n" + message;
        emailSender.sendMailToAdministrator(SUBJECT_NEW_MESSAGE, text);
    }
}
