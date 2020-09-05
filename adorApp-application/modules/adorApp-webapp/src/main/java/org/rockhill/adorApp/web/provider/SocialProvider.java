package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithAuditTrail;
import org.rockhill.adorApp.database.business.BusinessWithSocial;
import org.rockhill.adorApp.database.business.helper.enums.SocialStatusTypes;
import org.rockhill.adorApp.database.tables.AuditTrail;
import org.rockhill.adorApp.database.tables.Social;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class SocialProvider {

    private final Logger logger = LoggerFactory.getLogger(SocialProvider.class);

    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    BusinessWithSocial businessWithSocial;


    public Object getSocialListAsObject() {
        List<Social> socialList = businessWithSocial.getSocialList();
        return socialList;
    }

    public Object getSocialAsObject(final Long id) {
        Social s = businessWithSocial.getSocialById(id);
        return s;
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Social:Update:" + id.toString(), fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    public Long updateSocial(Social newSocial, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long id = newSocial.getId();
        Social social = businessWithSocial.getSocialById(id);
        if (newSocial == null) {
            //ERROR we shall update existing Socials only.
            return null;
        }
        Long refId = newSocial.getId();
        //personId
        Long newLongValue = newSocial.getPersonId();
        Long oldLongValue = social.getPersonId();
        if (!((oldLongValue == null) && (newLongValue == null))) { //if both null, it was not changed
            if ( ((oldLongValue == null) && (newLongValue != null)) //at least one of them is not null
                    || ((oldLongValue != null) && (newLongValue == null))
                    || (!oldLongValue.equals(newLongValue)) ) { //here both of them is not null
                social.setPersonId(newLongValue); //if we are here, it must have been changed
                String oldValue = "N/A";
                if (oldLongValue != null) {
                    oldValue = oldLongValue.toString();
                }
                String newValue = "N/A";
                if (newLongValue != null) {
                    newValue = newLongValue.toString();
                }
                auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "PersonId", oldValue, newValue));
            }
        }
        //socialStatus
        Integer newStatus = newSocial.getSocialStatus();
        Integer oldStatus = social.getSocialStatus();
        if (!oldStatus.equals(newStatus)) {
            social.setSocialStatus(newStatus);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "SocialStatus",
                    SocialStatusTypes.getTranslatedString(oldStatus), SocialStatusTypes.getTranslatedString(newStatus)));
        }
        //facebookUserName
        String newString = newSocial.getFacebookUserName();
        String oldString = social.getFacebookUserName();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setFacebookUserName(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "FacebookUserName",
                    oldString, newString));
        }
        //facebookFirstName
        newString = newSocial.getFacebookFirstName();
        oldString = social.getFacebookFirstName();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setFacebookFirstName(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "FacebookFirstName",
                    oldString, newString));
        }
        //facebookEmail
        newString = newSocial.getFacebookEmail();
        oldString = social.getFacebookEmail();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setFacebookEmail(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "FacebookEmail",
                    oldString, newString));
        }
        //facebookUserId
        newString = newSocial.getFacebookUserId();
        oldString = social.getFacebookUserId();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setFacebookUserId(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "FacebookUserId",
                    oldString, newString));
        }
        //googleUserName
        newString = newSocial.getGoogleUserName();
        oldString = social.getGoogleUserName();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setGoogleUserName(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "GoogleUserName",
                    oldString, newString));
        }
        //googleUserPicture
        newString = newSocial.getGoogleUserPicture();
        oldString = social.getGoogleUserPicture();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setGoogleUserPicture(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "GoogleUserPicture",
                    oldString, newString));
        }
        //googleEmail
        newString = newSocial.getGoogleEmail();
        oldString = social.getGoogleEmail();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setGoogleEmail(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "GoogleEmail",
                    oldString, newString));
        }
        //googleUserId
        newString = newSocial.getGoogleUserId();
        oldString = social.getGoogleUserId();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setGoogleUserId(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "GoogleUserId",
                    oldString, newString));
        }
        //comment
        newString = newSocial.getComment();
        oldString = social.getComment();
        if ((oldString == null) || (newString == null)) {
            logger.info("User:" + currentUserInformationJson.userName + " tried to create/update Social with empty string.");
            return null;
        }
        businessWithAuditTrail.checkDangerousValue(newString, currentUserInformationJson.userName);
        if (!oldString.contentEquals(newString)) {
            social.setComment(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "Comment",
                    oldString, newString));
        }
        id = businessWithSocial.updateSocial(social, auditTrailCollection);
        return id;
    }

    public Object getSocialHistoryAsObject(Long id) {
        List<AuditTrail> a = businessWithAuditTrail.getAuditTrailOfObject(id);
        return a;
    }

    public Long deleteSocial(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        Long id = Long.parseLong(p.entityId);
        Social social = businessWithSocial.getSocialById(id);
        //collect related audit records
        List<AuditTrail> auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(id);
        Long result = businessWithSocial.deleteSocial(social, auditTrailList);
        return result;
    }
}
