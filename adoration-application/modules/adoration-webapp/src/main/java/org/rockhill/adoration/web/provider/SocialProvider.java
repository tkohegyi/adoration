package org.rockhill.adoration.web.provider;

import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.database.business.BusinessWithSocial;
import org.rockhill.adoration.database.business.helper.enums.SocialStatusTypes;
import org.rockhill.adoration.database.tables.AuditTrail;
import org.rockhill.adoration.database.tables.Social;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.DeleteEntityJson;
import org.rockhill.adoration.web.provider.helper.ProviderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class to provide information about social users.
 */
@Component
public class SocialProvider extends ProviderBase {

    private final Logger logger = LoggerFactory.getLogger(SocialProvider.class);

    @Autowired
    private BusinessWithAuditTrail businessWithAuditTrail;
    @Autowired
    private BusinessWithSocial businessWithSocial;


    public Object getSocialListAsObject() {
        return businessWithSocial.getSocialList();
    }

    public Object getSocialAsObject(final Long id) {
        return businessWithSocial.getSocialById(id);
    }

    private AuditTrail prepareAuditTrail(Long id, String userName, String fieldName, String oldValue, String newValue) {
        AuditTrail auditTrail;
        auditTrail = businessWithAuditTrail.prepareAuditTrail(id, userName, "Social:Update:" + id.toString(), fieldName + " changed from:\"" + oldValue + "\" to:\"" + newValue + "\"", "");
        return auditTrail;
    }

    public Long updateSocial(Social proposedSocial, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long refId = proposedSocial.getId();
        Social targetSocial = businessWithSocial.getSocialById(refId);
        //personId
        Long newLongValue = proposedSocial.getPersonId();
        Long oldLongValue = targetSocial.getPersonId();
        if (isLongChanged(oldLongValue, newLongValue)) {
            targetSocial.setPersonId(newLongValue); //if we are here, it must have been changed
            String oldValue = prepareAuditValueString(oldLongValue);
            String newValue = prepareAuditValueString(newLongValue);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "PersonId", oldValue, newValue));
        }
        //socialStatus
        Integer newStatus = proposedSocial.getSocialStatus();
        Integer oldStatus = targetSocial.getSocialStatus();
        if (!oldStatus.equals(newStatus)) {
            targetSocial.setSocialStatus(newStatus);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "SocialStatus",
                    SocialStatusTypes.getTranslatedString(oldStatus), SocialStatusTypes.getTranslatedString(newStatus)));
        }
        if (!handleSocialUpdatePreparationFacebookPart(targetSocial, proposedSocial, auditTrailCollection, currentUserInformationJson.userName)) {
            return null;
        }
        if (!handleSocialUpdatePreparationGooglePart(targetSocial, proposedSocial, auditTrailCollection, currentUserInformationJson.userName)) {
            return null;
        }
        //comment
        String newString = proposedSocial.getComment();
        String oldString = targetSocial.getComment();
        if (!isSocialStringFieldChangeValid(oldString, newString, currentUserInformationJson.userName)) {
            return null;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setComment(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, currentUserInformationJson.userName, "Comment",
                    oldString, newString));
        }
        return businessWithSocial.updateSocial(targetSocial, auditTrailCollection);
    }

    private boolean handleSocialUpdatePreparationGooglePart(Social targetSocial, Social proposedSocial, Collection<AuditTrail> auditTrailCollection, String userName) {
        Long refId = proposedSocial.getId();
        //googleUserName
        String newString = proposedSocial.getGoogleUserName();
        String oldString = targetSocial.getGoogleUserName();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setGoogleUserName(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "GoogleUserName",
                    oldString, newString));
        }
        //googleUserPicture
        newString = proposedSocial.getGoogleUserPicture();
        oldString = targetSocial.getGoogleUserPicture();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setGoogleUserPicture(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "GoogleUserPicture",
                    oldString, newString));
        }
        //googleEmail
        newString = proposedSocial.getGoogleEmail();
        oldString = targetSocial.getGoogleEmail();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setGoogleEmail(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "GoogleEmail",
                    oldString, newString));
        }
        //googleUserId
        newString = proposedSocial.getGoogleUserId();
        oldString = targetSocial.getGoogleUserId();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setGoogleUserId(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "GoogleUserId",
                    oldString, newString));
        }
        return true;
    }

    private boolean handleSocialUpdatePreparationFacebookPart(Social targetSocial, Social proposedSocial, Collection<AuditTrail> auditTrailCollection, String userName) {
        Long refId = proposedSocial.getId();
        //facebookUserName
        String newString;
        newString = proposedSocial.getFacebookUserName();
        String oldString;
        oldString = targetSocial.getFacebookUserName();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setFacebookUserName(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "FacebookUserName",
                    oldString, newString));
        }
        //facebookFirstName
        newString = proposedSocial.getFacebookFirstName();
        oldString = targetSocial.getFacebookFirstName();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setFacebookFirstName(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "FacebookFirstName",
                    oldString, newString));
        }
        //facebookEmail
        newString = proposedSocial.getFacebookEmail();
        oldString = targetSocial.getFacebookEmail();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setFacebookEmail(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "FacebookEmail",
                    oldString, newString));
        }
        //facebookUserId
        newString = proposedSocial.getFacebookUserId();
        oldString = targetSocial.getFacebookUserId();
        if (!isSocialStringFieldChangeValid(oldString, newString, userName)) {
            return false;
        }
        if (!oldString.contentEquals(newString)) {
            targetSocial.setFacebookUserId(newString);
            auditTrailCollection.add(prepareAuditTrail(refId, userName, "FacebookUserId",
                    oldString, newString));
        }
        return true;
    }

    private boolean isSocialStringFieldChangeValid(String oldString, String newString, String userName) {
        if ((oldString == null) || (newString == null)) {
            logger.info("User: {} tried to create/update Social with null string.", userName);
            return false;
        }
        businessWithAuditTrail.checkDangerousValue(newString, userName);
        return true;
    }

    public Object getSocialHistoryAsObject(Long id) {
        List<AuditTrail> auditTrailOfObject;
        auditTrailOfObject = businessWithAuditTrail.getAuditTrailOfObject(id);
        return auditTrailOfObject;
    }

    public Long deleteSocial(DeleteEntityJson p, CurrentUserInformationJson currentUserInformationJson) {
        Long id = Long.parseLong(p.entityId);
        Social social = businessWithSocial.getSocialById(id);
        //collect related audit records
        List<AuditTrail> auditTrailList = businessWithAuditTrail.getAuditTrailOfObject(id);
        Long result;
        result = businessWithSocial.deleteSocial(social, auditTrailList);
        return result;
    }
}
