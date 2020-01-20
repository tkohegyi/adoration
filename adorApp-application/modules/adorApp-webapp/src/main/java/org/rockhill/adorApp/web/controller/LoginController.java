package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.rockhill.adorApp.web.configuration.WebAppConfigurationAccess;
import org.rockhill.adorApp.web.json.LoginUrlInformationJson;
import org.rockhill.adorApp.web.service.FacebookOauth2Service;
import org.rockhill.adorApp.web.service.GoogleOauth2Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * Controller for handling requests for the application login page.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class LoginController {
    private static final String JSON_LOGIN_URL_INFO = "loginUrlInfo";
    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    GoogleOauth2Service googleOauth2Service;
    @Autowired
    FacebookOauth2Service facebookOauth2Service;

    @Autowired
    WebAppConfigurationAccess webAppConfigurationAccess;

    /**
     * Serves requests which arrive to home and sends back the home page.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adoration/login", method = {RequestMethod.GET, RequestMethod.POST})
    public String showLoginPage(
            @RequestParam(value = "result", defaultValue = "") final String result
    ) {
        if (result.length() == 0) {
            return "login";
        }
        //else make simple login page default
        logger.warn("Unhandled login page request, falling back to provide basic login page.");
        return "login";
    }

    //https://fuf.me/adoration/loginResult?code=4%2FrgG8fzvTngq_gf3YiQgi5x8vGrZis4JD4SXyLxyVhHD97o-k13uxXJHmSqnBa5o-7y-QmjtgMZnyHryn4u_heR8&scope=email+profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+openid&authuser=0&session_state=8ca7cab3c0dd23415b112fae84f84b1cb9957590..dd73&prompt=consent#
    @RequestMapping(value = "/adoration/loginResult", method = {RequestMethod.GET, RequestMethod.POST})
    public String showLoginResultPage(
            @RequestParam(value = "code", defaultValue = "") final String code,  //google uses this
            @RequestParam(value = "scope", defaultValue = "") final String scope,
            @RequestParam(value = "authuser", defaultValue = "") final String authuser,
            @RequestParam(value = "state", defaultValue = "") final String state,  //facebook uses this
            @RequestParam(value = "access_token", defaultValue = "") final String access_token,
            HttpSession httpSession,
            HttpServletResponse httpServletResponse
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if ((code.length() > 0) && (state.length() == 0) && (auth == null)) {  //if GOOGLE login can be performed and it is not yet authenticated for Ador App
            Authentication authentication = googleOauth2Service.getGoogleUserInfoJson(code);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);
            httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            try {
                httpServletResponse.sendRedirect(webAppConfigurationAccess.getProperties().getGoogleRedirectUrl());
            } catch (IOException e) {
                logger.warn("Redirect after Google authentication does not work.", e);
                return "login";
            }
        }
        if ((code.length() > 0) && (state.length() > 0) && (auth == null)) {  //if FACEBOOK login can be performed and it is not yet authenticated for Ador App
            Authentication authentication = facebookOauth2Service.getFacebookUserInfoJson(code);
            SecurityContext sc = SecurityContextHolder.getContext();
            sc.setAuthentication(authentication);
            httpSession.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);
            try {
                httpServletResponse.sendRedirect(webAppConfigurationAccess.getProperties().getGoogleRedirectUrl());
            } catch (IOException e) {
                logger.warn("Redirect after Facebook authentication does not work.", e);
                return "login";
            }
        }
        return "loginResult";
    }

    @RequestMapping(value = "/adorationSecure/exit", method = {RequestMethod.GET, RequestMethod.POST})
    public String showExitPage(
            HttpSession httpSession,
            HttpServletResponse httpServletResponse
    ) {
        //clean up the session info
        SecurityContext sc = (SecurityContext) httpSession.getAttribute(SPRING_SECURITY_CONTEXT_KEY);
        if (sc != null) {
            sc.setAuthentication(null); // this cleans up the authentication data technically
            httpSession.removeAttribute(SPRING_SECURITY_CONTEXT_KEY); // this clean up the session itself
        }
        try {
            httpServletResponse.sendRedirect(webAppConfigurationAccess.getProperties().getBaseUrl());
        } catch (IOException e) {
            logger.warn("Redirect after logout does not work.", e);
        }
        return "home";
    }

    @ResponseBody
    @RequestMapping(value = "/adoration/getLoginUrls", method = {RequestMethod.GET})
    public Map<String, Collection<String>> getLoginUrls() {

        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        LoginUrlInformationJson loginUrlInformationJson = new LoginUrlInformationJson();
        loginUrlInformationJson = googleOauth2Service.addLoginUrlInformation(loginUrlInformationJson);
        loginUrlInformationJson = facebookOauth2Service.addLoginUrlInformation(loginUrlInformationJson);

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("details", gson.toJsonTree(loginUrlInformationJson, new TypeToken<LoginUrlInformationJson>() {}.getType()));
        String json = gson.toJson(jsonObject);
        jsonString.add(json);
        jsonResponse.put(JSON_LOGIN_URL_INFO, jsonString);
        return jsonResponse;
    }

}
