package org.rockhill.adoration.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.rockhill.adoration.web.json.CoverageInformationJson;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.CoverageProvider;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for handling requests for the application home page.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class HomeController {
    private static final String JSON_LOGGED_IN_USER_INFO = "loggedInUserInfo";
    private static final String JSON_COVERAGE_INFO = "coverageInfo";

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private CoverageProvider coverageProvider;

    /**
     * Serves requests which arrive to home and sends back the home page.
     *
     * @return the name of the jsp to display as result
     */
    @GetMapping(value = "/")
    public String pseudoHome() {
        return "redirect:/adoration/";
    }

    /**
     * Serving the Home page request.
     *
     * @return with proper content
     */
    @GetMapping(value = "/adoration/")
    public String realHome() {
        return "home";
    }

    /**
     * Serving "favicon.ico" request.
     *
     * @return with proper content
     */
    @GetMapping(value = "/favicon.ico")
    public String favicon() {
        return "redirect:/resources/img/favicon.ico";
    }

    /**
     * Serving "robots.txt" request.
     *
     * @return with the proper content
     */
    @GetMapping(value = "/robots.txt")
    public String robots() {
        return "redirect:/resources/robots.txt";
    }

    /**
     * Serving the ".well-known/security.txt" request.
     *
     * @return with the proper content
     */
    @GetMapping(value = "/.well-known/security.txt")
    public String securityText() {
        return "redirect:/resources/security.txt";
    }

    /**
     * Grace handling of E404 (File not found) issues.
     *
     * @param httpServletRequest is the request
     * @return with proper content
     */
    @GetMapping(value = "/adoration/e404")
    public String e404(HttpServletRequest httpServletRequest) {
        return "E404";
    }

    /**
     * Grace handling of E500 (Internal Server Error) issues.
     *
     * @param httpServletRequest is the request
     * @return with proper content
     */
    @GetMapping(value = "/adoration/e500")
    public String e500(HttpServletRequest httpServletRequest) {
        return "E404";
    }

    /**
     * Serves information about the logged in user.
     *
     * @param httpSession         is the session of the user
     * @param httpServletResponse to be used for the response
     * @return with proper content
     * @throws IOException in case exception occurs
     */
    @ResponseBody
    @GetMapping(value = "/adoration/getLoggedInUserInfo")
    public Map<String, Collection<String>> getLoggedInUserInfo(HttpSession httpSession, HttpServletResponse httpServletResponse
    ) throws IOException {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(currentUserInformationJson);

        jsonString.add(json);
        jsonResponse.put(JSON_LOGGED_IN_USER_INFO, jsonString);
        return jsonResponse;
    }

    /**
     * Serves full information about the coverage information.
     *
     * @param httpSession         is the session of the user
     * @param httpServletResponse to use it for the response
     * @return with proper content
     * @throws IOException in case error occurs
     */
    @ResponseBody
    @GetMapping(value = "/adoration/getCoverageInformation")
    public Map<String, Collection<String>> getCoverageInformation(HttpSession httpSession, HttpServletResponse httpServletResponse) throws IOException {

        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");

        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        CoverageInformationJson coverageInformationJson = coverageProvider.getCoverageInfo(currentUserInformationJson);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(coverageInformationJson);

        jsonString.add(json);
        jsonResponse.put(JSON_COVERAGE_INFO, jsonString);
        return jsonResponse;
    }

}
