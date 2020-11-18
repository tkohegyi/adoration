package org.rockhill.adoration.web.controller;

import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.CoverageInformationJson;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.CoverageProvider;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests for the application home page.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class HomeController extends ControllerBase {
    private static final String JSON_LOGGED_IN_USER_INFO = "loggedInUserInfo";
    private static final String JSON_COVERAGE_INFO = "coverageInfo";
    private final Logger logger = LoggerFactory.getLogger(HomeController.class);

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
        return REDIRECT_TO_HOME;
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
    @RequestMapping(value = "/adoration/e404", method = {RequestMethod.GET, RequestMethod.POST})
    public String e404(HttpServletRequest httpServletRequest) {
        logger.warn("E404 caused by: {} / method: {}",
                httpServletRequest.getRemoteHost(), httpServletRequest.getMethod());
        return "E404";
    }

    /**
     * Grace handling of E500 (Internal Server Error) issues.
     *
     * @param httpServletRequest is the request
     * @return with proper content
     */
    @RequestMapping(value = "/adoration/e500", method = {RequestMethod.GET, RequestMethod.POST})
    public String e500(HttpServletRequest httpServletRequest) {
        logger.warn("E500 caused by: {} / method: {}",
                httpServletRequest.getRemoteHost(), httpServletRequest.getMethod());
        return "E404";
    }

    /**
     * Serves information about the logged in user.
     *
     * @param httpSession         is the session of the user
     * @return with proper content
     */
    @ResponseBody
    @GetMapping(value = "/adoration/getLoggedInUserInfo")
    public ResponseEntity<String> getLoggedInUserInfo(HttpSession httpSession) {
        ResponseEntity<String> result;
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        result = buildResponseBodyResult(JSON_LOGGED_IN_USER_INFO, currentUserInformationJson, HttpStatus.OK);
        return result;
    }

    /**
     * Serves full information about the coverage information.
     *
     * @param httpSession         is the session of the user
     * @return with proper content
     */
    @ResponseBody
    @GetMapping(value = "/adoration/getCoverageInformation")
    public ResponseEntity<String> getCoverageInformation(HttpSession httpSession) {
        ResponseEntity<String> result;
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        CoverageInformationJson coverageInformationJson = coverageProvider.getCoverageInfo(currentUserInformationJson);
        result = buildResponseBodyResult(JSON_COVERAGE_INFO, coverageInformationJson, HttpStatus.OK);
        return result;
    }

}
