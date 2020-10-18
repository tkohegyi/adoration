package org.rockhill.adoration.web.service;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.rockhill.adoration.database.business.BusinessWithAuditTrail;
import org.rockhill.adoration.database.business.BusinessWithNextGeneralKey;
import org.rockhill.adoration.database.business.BusinessWithPerson;
import org.rockhill.adoration.database.business.BusinessWithSocial;
import org.rockhill.adoration.database.business.helper.enums.SocialStatusTypes;
import org.rockhill.adoration.database.tables.AuditTrail;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.database.tables.Social;
import org.rockhill.adoration.exception.SystemException;
import org.rockhill.adoration.helper.EmailSender;
import org.rockhill.adoration.web.configuration.PropertyDto;
import org.rockhill.adoration.web.configuration.WebAppConfigurationAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * For Handling proper Facebook Oauth2 authorization tasks.
 */
@Component
public class FacebookOauth2Service {

    private final Logger logger = LoggerFactory.getLogger(FacebookOauth2Service.class);
    private final String subject = "[AdoratorApp] - Új Facebook Social";

    private static final String GRAPH_URL = "https://graph.facebook.com/v5.0/oauth/access_token?";
    private static final String AUTHORIZATION_URL = "https://www.facebook.com/v5.0/dialog/oauth?";

    private FacebookConnectionFactory facebookConnectionFactory;

    @Autowired
    AdorationCustomAuthenticationProvider adorationCustomAuthenticationProvider;

    @Autowired
    WebAppConfigurationAccess webAppConfigurationAccess;

    @Autowired
    BusinessWithSocial businessWithSocial;

    @Autowired
    BusinessWithPerson businessWithPerson;

    @Autowired
    BusinessWithAuditTrail businessWithAuditTrail;

    @Autowired
    BusinessWithNextGeneralKey businessWithNextGeneralKey;

    @Autowired
    EmailSender emailSender;

    @PostConstruct
    private void FacebookOauth2Service() {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        facebookConnectionFactory = new FacebookConnectionFactory(propertyDto.getFacebook_app_id(), propertyDto.getFacebook_app_secret());
        facebookConnectionFactory.setScope("email,public_profile");
    }

    public String getLoginUrlInformation() {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();

        String authorizationUrl = AUTHORIZATION_URL
                + "client_id=" + propertyDto.getFacebook_app_id()
                + "&redirect_uri=" + propertyDto.getGoogleRedirectUrl()
                + "&state=no-state&display=popup&response_type=code&scope=" + facebookConnectionFactory.getScope();
        //note use this: &response_type=granted_scopes to get list of granted scopes
        return authorizationUrl;
    }

    public String getFacebookGraphUrl(final String code, final String applicationId, final String applicationSecret, final String redirectUrl) throws UnsupportedEncodingException {
        String fbGraphUrl;
        fbGraphUrl = GRAPH_URL
                + "client_id=" + applicationId
                + "&redirect_uri=" + URLEncoder.encode(redirectUrl, "UTF-8")
                + "&client_secret=" + applicationSecret
                + "&code=" + code;
        return fbGraphUrl;
    }

    public String getAccessToken(String code, String applicationId, String applicationSecret, String redirectUrl) throws IOException, ParseException {
        URL fbGraphURL;
        String fbGraphURLString = getFacebookGraphUrl(code, applicationId, applicationSecret, redirectUrl);
        fbGraphURL = new URL(fbGraphURLString);
        URLConnection fbConnection = fbGraphURL.openConnection(); //NOSONAR - code is properly protected
        BufferedReader in = new BufferedReader(new InputStreamReader(fbConnection.getInputStream()));
        String inputLine;
        StringBuffer b = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            b.append(inputLine + "\n");
        }
        in.close();

        String accessToken = b.toString();
        JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
        JSONObject json = (JSONObject) parser.parse(accessToken);
        accessToken = json.getAsString("access_token");
        return accessToken;
    }

    public JSONObject getFacebookGraph(String accessToken) {
        String graph = null;
        JSONObject json = null;
        try {
            String g = "https://graph.facebook.com/me?access_token=" + accessToken + "&fields=name,id,email";
            URL u = new URL(g);
            URLConnection c = u.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    c.getInputStream()));
            String inputLine;
            StringBuffer b = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                b.append(inputLine + "\n");
            in.close();
            graph = b.toString();
            JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
            json = (JSONObject) parser.parse(graph);
        } catch (Exception e) {
            throw new SystemException("ERROR in getting FB graph data. ", e);
        }

        return json;
    }

    public Authentication getFacebookUserInfoJson(final String authCode) {
        FacebookUser facebookUser = null;
        Authentication authentication = null;
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        try {
            String accessToken = getAccessToken(authCode, propertyDto.getFacebook_app_id(), propertyDto.getFacebook_app_secret(), propertyDto.getGoogleRedirectUrl());
            JSONObject facebookUserInfoJson = getFacebookGraph(accessToken);
            Social social = detectSocial(facebookUserInfoJson);
            Person person = detectPerson(social);
            facebookUser = new FacebookUser(social, person, propertyDto.getSessionTimeout());

            // googleUser used as Principal, credential is coming from Google
            authentication = adorationCustomAuthenticationProvider.authenticate(new PreAuthenticatedAuthenticationToken(facebookUser, facebookUserInfoJson));
        } catch (Exception e) {
            logger.warn("Was unable to get Google User Information.", e);
        }
        return authentication;
    }

    private Person detectPerson(Social social) {
        Person person = null;
        Long personId = social.getPersonId();
        if (personId != null) {
            person = businessWithPerson.getPersonById(personId);
        }
        return person;
    }

    private Social detectSocial(JSONObject facebookUserInfoJson) {
        String userId = facebookUserInfoJson.getAsString("id");
        String email = facebookUserInfoJson.getAsString("email");
        if (email == null) {
            email = "";
        }
        String firstName = facebookUserInfoJson.getAsString("name");
        if (firstName == null) {
            firstName = "";
        }
        Social social = businessWithSocial.getSocialByFUserId(userId);
        if (social == null) {
            social = new Social();
            social.setFacebookUserId(userId);
            social.setFacebookEmail(email);
            social.setFacebookFirstName(firstName);
            social.setFacebookUserName(social.getFacebookFirstName());  // this is what we can access by default...
            social.setSocialStatus(SocialStatusTypes.WAIT_FOR_IDENTIFICATION.getTypeValue());
            Long id = businessWithNextGeneralKey.getNextGeneralId();
            social.setId(id);
            AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getFacebookUserName(), "Social:New:" + id.toString(),
                    "New Facebook Social login created.", "Facebook");
            //this is a brand new login, try to identify - by using e-mail
            if ( (email != null) && (email.length() > 0) ) {
                Person p = businessWithPerson.getPersonByEmail(email);
                if (p != null) { // we were able to identify the person by e-mail
                    social.setPersonId(p.getId());
                    social.setSocialStatus(SocialStatusTypes.IDENTIFIED_USER.getTypeValue());
                }
            }
            String text = "New Social id: " + id.toString() + "\nFacebook Type,\n Name: " + social.getFacebookUserName() + ",\nEmail: " + social.getFacebookEmail();
            emailSender.sendMail(subject, text); //send mail to administrator
            text = "Kedves " + social.getFacebookUserName() + "!\n\nKöszönettel vettük első bejelentkezésedet a Váci Örökimádás (https://orokimadas.info:9092/) weboldalán.\n\nA következő adatokat ismertük meg rólad:"
                    + "\nNév: " + social.getFacebookUserName()
                    + "\nE-mail: " + social.getFacebookEmail()
                    + "\nFacebook azonosító: " + social.getFacebookUserId()
                    + "\n\nAdatkezelési tájékoztatónkat megtalálhatod itt: https://orokimadas.info:9092/resources/img/AdatkezelesiSzabalyzat.pdf"
                    + "\nAdataidról információt illetve azok törlését pedig erre az e-mail címre írva kérheted: kohegyi.tamas (kukac) vac-deakvar.vaciegyhazmegye.hu."
                    + "\nUgyanezen a címen várjuk leveledet akkor is, ha kérdésed, észrevételed vagy javaslatod van a weboldallal kapcsolatban. "
                    + "\n\nAmennyiben már regisztrált adoráló vagy, erre a levélre válaszolva kérlek írd meg, hogy mikor szoktál az Örökimádásban részt venni, vagy a telefonszámodat, hogy felvehessük veled a kapcsolatot."
                    + "\n\nÜdvözlettel:\nKőhegyi Tamás\naz örökimádás világi koordinátora\n+36-70-375-4140\n";
            emailSender.sendMailSpecial(social.getGoogleEmail(), "Belépés az Örökimádás weboldalán Facebook azonosítóval", text); //send feedback mail to the registered user
            id = businessWithSocial.newSocial(social, auditTrail);
            social.setId(id); //Social object is ready
        } else {
            //detect social update and act
            Collection<AuditTrail> auditTrailCollection = new ArrayList<>();
            if (social.getFacebookFirstName().compareToIgnoreCase(firstName) != 0) {
                social.setFacebookFirstName(firstName);
                social.setFacebookUserName(firstName);  // this is what we can access by default...
                Long id = businessWithNextGeneralKey.getNextGeneralId();
                AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getFacebookUserName(), "Social:Update:" + social.getId().toString(),
                        "Facebook FirstName/Name updated to:" + firstName, "Facebook");
                auditTrailCollection.add(auditTrail);
            }
            if (social.getFacebookEmail().compareToIgnoreCase(email) != 0) {
                social.setFacebookEmail(email);
                Long id = businessWithNextGeneralKey.getNextGeneralId();
                AuditTrail auditTrail = businessWithAuditTrail.prepareAuditTrail(id, social.getFacebookUserName(), "Social:Update:" + social.getId().toString(),
                        "Facebook Email updated to:" + email, "Facebook");
                auditTrailCollection.add(auditTrail);
            }
            if (!auditTrailCollection.isEmpty()) {
                businessWithSocial.updateSocial(social, auditTrailCollection);
            }
        }
        return social;
    }

}
