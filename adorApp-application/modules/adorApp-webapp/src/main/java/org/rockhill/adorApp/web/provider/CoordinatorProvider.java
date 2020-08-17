package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.*;
import org.rockhill.adorApp.database.business.helper.enums.CoordinatorTypes;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Coordinator;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.json.CoordinatorJson;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.*;

@Component
public class CoordinatorProvider {

    private final Logger logger = LoggerFactory.getLogger(CoordinatorProvider.class);

    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    BusinessWithCoordinator businessWithCoordinator;
    @Autowired
    BusinessWithPerson businessWithPerson;
    @Autowired
    BusinessWithNextGeneralKey businessWithNextGeneralKey;
    @Autowired
    BusinessWithTranslator businessWithTranslator;

    private CoordinatorJson getCoordinatorJsonFromCoordinator(CurrentUserInformationJson currentUserInformationJson, Coordinator coordinator) {
        CoordinatorJson coordinatorJson = new CoordinatorJson();
        coordinatorJson.id = coordinator.getId().toString();
        coordinatorJson.coordinatorType = coordinator.getCoordinatorType().toString();
        coordinatorJson.coordinatorTypeText = businessWithTranslator.getTranslatorValue(currentUserInformationJson.languageCode,
                "COORDINATOR-" + coordinatorJson.coordinatorType, "N/A");
        if (coordinator.getPersonId() != null) {
            coordinatorJson.personId = coordinator.getPersonId().toString();
            Person p = businessWithPerson.getPersonById(coordinator.getPersonId());
            coordinatorJson.personName = p.getName();
            coordinatorJson.phone = p.getMobile();
            coordinatorJson.eMail = p.getEmail();
            coordinatorJson.visibleComment = p.getVisibleComment();
        } else {
            coordinatorJson.personId = "";
            coordinatorJson.personName = "";
            coordinatorJson.phone = "";
            coordinatorJson.eMail = "";
            coordinatorJson.visibleComment = "";
        }
        return coordinatorJson;
    }

    public Object getCoordinatorListAsObject(CurrentUserInformationJson currentUserInformationJson) {
        List<Coordinator> coordinatorList = getCoordinatorList();
        List<CoordinatorJson> coordinatorJsonList = new LinkedList<>();
        for (Coordinator coordinator: coordinatorList) {
            CoordinatorJson coordinatorJson = getCoordinatorJsonFromCoordinator(currentUserInformationJson, coordinator);
            coordinatorJsonList.add(coordinatorJson);
        }
        return coordinatorJsonList;
    }

    public List<Coordinator> getCoordinatorList() {
        List<Coordinator> coordinatorList = businessWithCoordinator.getList();
        return coordinatorList;
    }

    public Object getCoordinatorAsObject(final Long id, CurrentUserInformationJson currentUserInformationJson) {
        Coordinator coordinator = businessWithCoordinator.getById(id);
        CoordinatorJson coordinatorJson = getCoordinatorJsonFromCoordinator(currentUserInformationJson, coordinator);
        return coordinatorJson;
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Coordinator:Update:" + id.toString(), fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    public Long updateCoordinator(CoordinatorJson coordinatorJson, CurrentUserInformationJson currentUserInformationJson) {
        Coordinator newCoordinator = new Coordinator();
        newCoordinator.setId(Long.parseLong(coordinatorJson.id));
        newCoordinator.setCoordinatorType(CoordinatorTypes.getTypeFromId(Integer.parseInt(coordinatorJson.coordinatorType)).getCoordinatorValue());
        if (coordinatorJson.personId.length() == 0) {
            newCoordinator.setPersonId(null);
        } else {
            newCoordinator.setPersonId(Long.parseLong(coordinatorJson.personId));
        }
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long id = newCoordinator.getId();
        Coordinator coordinator = businessWithCoordinator.getById(id);
        if (coordinator == null) {
            coordinator = new Coordinator();
            coordinator.setId(businessWithNextGeneralKey.getNextGeneralId());
            coordinator.setCoordinatorType(newCoordinator.getCoordinatorType());
            Long personId = newCoordinator.getPersonId();
            String personString = "N/A";
            if (personId != null) {
                Person person = businessWithPerson.getPersonById(personId);
                if (person == null) {
                    logger.info("User:" + currentUserInformationJson.userName + " tried to create Coordinator with non-existing person.");
                    return null;
                }
                personString = "ID: " + person.getId().toString() + ", Name: " + person.getName();
            }
            coordinator.setPersonId(personId);
            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(coordinator.getId(), currentUserInformationJson.userName,
                    "Coordinator:New:" + coordinator.getId(),
                    "CoordinatorType: " + CoordinatorTypes.getTypeFromId(coordinator.getCoordinatorType()).toString() + ", Person: " + personString, "");
            id = businessWithCoordinator.newCoordinator(coordinator, auditTrail);
            return id;
        }
        Long refId = coordinator.getId();
        //coordinatorType
        Integer newIntValue = newCoordinator.getCoordinatorType();
        Integer oldIntValue = coordinator.getCoordinatorType();
        if (newIntValue != oldIntValue) {
            CoordinatorTypes.getTypeFromId(newIntValue); //validation of value
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "CoordinatorType",
                    CoordinatorTypes.getTypeFromId(oldIntValue).toString(),
                    CoordinatorTypes.getTypeFromId(newIntValue).toString()));
        }
        //personId
        Long newLongValue = newCoordinator.getPersonId();
        Long oldLongValue = coordinator.getPersonId();
        if (!((oldLongValue == null) && (newLongValue == null))) { //if both null, it was not changed
            if ( ((oldLongValue == null) && (newLongValue != null)) //at least one of them is not null
                    || ((oldLongValue != null) && (newLongValue == null))
                    || (!oldLongValue.equals(newLongValue)) ) { //here both of them is not null
                String oldValue = "N/A";
                if (oldLongValue != null) {
                    oldValue = oldLongValue.toString();
                }
                String newValue = "N/A";
                if (newLongValue != null) {
                    newValue = newLongValue.toString();
                }
                if (newLongValue != null) {
                    Person person = businessWithPerson.getPersonById(newLongValue);
                    if (person == null) {
                        logger.info("User:" + currentUserInformationJson.userName + " tried to update Coordinator with non-existing person.");
                        return null;
                    }
                }
                auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "PersonId", oldValue, newValue));
            }
        }
        id = businessWithCoordinator.updateCoordinator(newCoordinator, auditTrailCollection);
        return id;
    }

    public Object getCoordinatorHistoryAsObject(Long id) {
        List<AuditTrail> a = businessWithAuditTrail.getAuditTrailOfObject(id);
        return a;
    }

    public Long deleteCoordinator(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        Long id = Long.parseLong(p.entityId);
        Coordinator coordinator = businessWithCoordinator.getById(id);
        //collect related audit records
        List<AuditTrail> auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(id);
        Long result = businessWithCoordinator.deleteCoordinator(coordinator, auditTrailList);
        return result;
    }

    public List<CoordinatorJson> getLeadership(CurrentUserInformationJson currentUserInformationJson) {
        List<Coordinator> coordinatorList = businessWithCoordinator.getList();
        //sort here by type
        coordinatorList.sort(Comparator.comparing(Coordinator::getCoordinatorType));
        List<CoordinatorJson> coordinatorJsonList = new LinkedList<>();
        for (Coordinator coordinator: coordinatorList) {
            CoordinatorJson coordinatorJson = getCoordinatorJsonFromCoordinator(currentUserInformationJson, coordinator);
            coordinatorJsonList.add(coordinatorJson);
        }
        return coordinatorJsonList;
    }
}
