package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.rockhill.adorApp.web.json.CoverageInformationJson;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.provider.CoverageProvider;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.web.context.HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY;

/**
 * Controller for handling requests for the application home page.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class HomeController {
    private static final String JSON_LOGGED_IN_USER_INFO = "loggedInUserInfo";
    private static final String JSON_COVERAGE_INFO = "coverageInfo";
    private static final String JSON_APP_INFO = "adorAppApplication";
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    CurrentUserProvider currentUserProvider;
    @Autowired
    CoverageProvider coverageProvider;

    /**
     * Serves requests which arrive to home and sends back the home page.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public String pseudoHome() {
        return "redirect:/adoration/";
    }

    @RequestMapping(value = "/adoration/", method = {RequestMethod.GET, RequestMethod.POST})
    public String realHome() {
        return "home";
    }

    @RequestMapping(value = "/adorationSecure/myInfoMyAccounts", method = RequestMethod.GET)
    public String myInfoMyAccounts() {
        return "myInfoMyAccounts";
    }

    @RequestMapping(value = "/adoration/myInfoMyProfile", method = RequestMethod.GET)
    public String myInfoMyProfile() {
        return "myInfoMyProfile";
    }

    @ResponseBody
    @RequestMapping(value = "/adoration/getLoggedInUserInfo", method = {RequestMethod.GET})
    public Map<String, Collection<String>> getLoggedInUserInfo(HttpSession httpSession, HttpServletResponse httpServletResponse
    ) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("details", gson.toJsonTree(currentUserInformationJson));
        String json = gson.toJson(jsonObject);
        jsonString.add(json);
        jsonResponse.put(JSON_LOGGED_IN_USER_INFO, jsonString);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/adoration/getAdorAppServerInfo", method = {RequestMethod.GET})
    public Map<String, Collection<String>> getAdorAppServerInfo() {

        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();
        InetAddress ip;
        String hostname;
        try {
            ip = InetAddress.getLocalHost(); //ip address
            hostname = ip.getHostName();
            jsonObject.add("ip", gson.toJsonTree(ip.toString()));
            jsonObject.add("hostname", gson.toJsonTree(hostname));
        } catch (UnknownHostException e) {
            logger.info("Login page - cannot detect ip/hostname.");
        }
        String json = gson.toJson(jsonObject);
        jsonString.add(json);
        jsonResponse.put(JSON_APP_INFO, jsonString);
        return jsonResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/adoration/getCoverageInformation", method = {RequestMethod.GET})
    public Map<String, Collection<String>> getCoverageInformation(HttpSession httpSession, HttpServletResponse httpServletResponse) {

        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");

        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        CoverageInformationJson coverageInformationJson = coverageProvider.getCoverageInfo(currentUserInformationJson);

        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("details", gson.toJsonTree(coverageInformationJson));
        String json = gson.toJson(jsonObject);
        jsonString.add(json);
        jsonResponse.put(JSON_COVERAGE_INFO, jsonString);
        return jsonResponse;
    }

}
