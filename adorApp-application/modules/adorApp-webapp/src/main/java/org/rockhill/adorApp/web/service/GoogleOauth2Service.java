package org.rockhill.adorApp.web.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.BusinessWithSocial;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.database.tables.Social;
import org.rockhill.adorApp.web.configuration.PropertyDto;
import org.rockhill.adorApp.web.configuration.WebAppConfigurationAccess;
import org.rockhill.adorApp.database.json.GoogleUserInfoJson;
import org.rockhill.adorApp.web.json.LoginUrlInformationJson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

/**
 * For Handling proper Google Oauth2 authorization tasks.
 */
@Component
public class GoogleOauth2Service {

    private final Logger logger = LoggerFactory.getLogger(GoogleOauth2Service.class);


    private static final String USER_INFO_URL = "https://www.googleapis.com/oauth2/v1/userinfo";
    private static final List<String> SCOPES = Arrays.asList(
            "https://www.googleapis.com/auth/userinfo.profile",
            "https://www.googleapis.com/auth/userinfo.email");
    private GoogleAuthorizationCodeFlow flow;
    private final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    @Autowired
    //private AuthenticationManager authenticationManager;
    AdorationCustomAuthenticationProvider adorationCustomAuthenticationProvider;

    @Autowired
    WebAppConfigurationAccess webAppConfigurationAccess;

    @Autowired
    BusinessWithSocial businessWithSocial;

    @Autowired
    BusinessWithPerson businessWithPerson;


    @PostConstruct
    private void GoogleOauth2Service() {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
                new JacksonFactory(), propertyDto.getGoogleClientId(), propertyDto.getGoogleClientSecret(), SCOPES).build();
    }

    /**
     * Checks the content of the provided loginInformation, and in case Google info is missing, then this method adds that
     * @param loginUrlInformationJson is the initial information json
     * @return with information Json that holds proper Google Login url
     */
    public LoginUrlInformationJson addLoginUrlInformation(LoginUrlInformationJson loginUrlInformationJson) {
        String name = "googleLoginAnchor";
        if (!loginUrlInformationJson.loginUrls.containsKey(name)) {

            //see help from https://www.programcreek.com/java-api-examples/?api=com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
            //see help from https://www.programcreek.com/java-api-examples/index.php?api=com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl

            GoogleClientSecrets.Details installedDetails = new GoogleClientSecrets.Details();
            PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
            installedDetails.setClientId(propertyDto.getGoogleClientId());
            installedDetails.setClientSecret(propertyDto.getGoogleClientSecret());

            GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
            clientSecrets.setInstalled(installedDetails);

            GoogleAuthorizationCodeRequestUrl googleAuthorizationCodeRequestUrl =
                    new GoogleAuthorizationCodeRequestUrl(clientSecrets, propertyDto.getGoogleRedirectUrl(), SCOPES);
            loginUrlInformationJson.loginUrls.put(name, googleAuthorizationCodeRequestUrl.build());
        }

        return loginUrlInformationJson;
    }

    public Authentication getGoogleUserInfoJson(final String authCode) {
        GoogleUser googleUser = null;
        Authentication authentication = null;
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        try {
            final GoogleTokenResponse response = flow.newTokenRequest(authCode)
                    .setRedirectUri(propertyDto.getGoogleRedirectUrl())
                    .execute();
            final Credential credential = flow.createAndStoreCredential(response, null);
            final HttpRequest request = HTTP_TRANSPORT.createRequestFactory(credential)
                    .buildGetRequest(new GenericUrl(USER_INFO_URL));
            request.getHeaders().setContentType("application/json");

            Gson gson = new Gson();
            GoogleUserInfoJson googleUserInfoJson = gson.fromJson(request.execute().parseAsString(), GoogleUserInfoJson.class);

            Social social = detectSocial(googleUserInfoJson);
            Person person = detectPerson(social);
            googleUser = new GoogleUser(social, person, propertyDto.getSessionTimeout());

            //googleUser used as Principal, credential is coming from Google
            authentication = adorationCustomAuthenticationProvider.authenticate(new PreAuthenticatedAuthenticationToken(googleUser, credential));
        } catch (Exception ex) {
            logger.warn("Was unable to get Google User Information.", ex);
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

    private Social detectSocial(GoogleUserInfoJson googleUserInfoJson) {
        Social social = businessWithSocial.getSocialByGUserId(googleUserInfoJson.id);
        if (social == null) {
            social = new Social();
            social.setGoogleEmail(googleUserInfoJson.email);
            social.setGoogleUserName(googleUserInfoJson.name);
            social.setGoogleUserId(googleUserInfoJson.id);
            social.setGoogleUserPicture(googleUserInfoJson.picture);

            //this is a brand new login, try to identify - by using e-mail
            if ( (googleUserInfoJson.email != null) && (googleUserInfoJson.email.length() > 0) ) {
                Person p = businessWithPerson.getPersonByEmail(googleUserInfoJson.email);
                if (p != null) { // we were able to identify the person by e-mail
                    social.setPersonId(p.getId());
                }
            }
            Long id = businessWithSocial.newSocial(social);
            social.setId(id); //Social object is ready
        }
        return social;
    }

}
