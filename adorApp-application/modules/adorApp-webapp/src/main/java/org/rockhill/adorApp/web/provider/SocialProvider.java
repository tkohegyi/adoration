package org.rockhill.adorApp.web.provider;

import org.rockhill.adorApp.database.business.BusinessWithAuditTrail;
import org.rockhill.adorApp.database.business.BusinessWithSocial;
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

    public Long updateSocial(Social s, CurrentUserInformationJson currentUserInformationJson) {
        Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
        Long id = s.getId();
        Social social = businessWithSocial.getSocialById(id);
        if (social == null) {
            //ERROR we shall update existing Socials only.
            return null;
        }
        //TODO
        return null;
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
