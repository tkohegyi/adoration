package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import org.rockhill.adorApp.exception.SystemException;
import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.MessageToCoordinatorJson;
import org.rockhill.adorApp.web.json.TableDataInformationJson;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.rockhill.adorApp.web.provider.InformationProvider;
import org.rockhill.adorApp.web.provider.PeopleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests for the application pages about Information.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class InformationController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(InformationController.class);
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private InformationProvider informationProvider;
    @Autowired
    private PeopleProvider peopleProvider;

    /**
     * Serves requests for general Information.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adorationSecure/information", method = {RequestMethod.GET, RequestMethod.POST})
    public String informationPage(HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        if (currentUserInformationJson.isRegisteredAdorator) { //registered adorator
            return "information";
        }
        if (currentUserInformationJson.isLoggedIn) { //can be waiting for identification or guest
            return "infoGuest";
        }
        return "redirect:/adoration/"; //not even logged in -> go back to basic home page
    }

    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getInformation", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getInformation(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        TableDataInformationJson content = null;
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            //has right to collect and see information
            Object information = informationProvider.getInformation(currentUserProvider.getUserInformation(httpSession), httpSession);
            content = new TableDataInformationJson(information);
        }
        return content;
    }

    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getGuestInformation", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getProfileInformation(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        TableDataInformationJson content = null;
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        if (currentUserInformationJson.isRegisteredAdorator) { //registered adorator
            //we should not be here, this area for Guests only
            logger.warn("Registered adorator: " + currentUserInformationJson.personId.toString() + " reached hidden area, pls check!");
            content = new TableDataInformationJson(null);
        } else if (currentUserInformationJson.isLoggedIn) { //can be waiting for identification or guest
            Object information = informationProvider.getGuestInformation(currentUserInformationJson, httpSession);
            content = new TableDataInformationJson(information);
        }
        return content;
    }

    /**
     * Serves requests to send message to main coordinator(s).
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/messageToCoordinator", method = RequestMethod.POST)
    public ResponseEntity<String> messageToCoordinator(@RequestBody final String body, final HttpSession httpSession) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
            Gson g = new Gson();
            MessageToCoordinatorJson p = g.fromJson(body, MessageToCoordinatorJson.class);
            //authorization is irrelevant, just the login status
            if (currentUserInformationJson.isLoggedIn) { //anybody who logged in can send message to maintainers
                peopleProvider.messageToCoordinator(p, currentUserInformationJson);
                resultString = "OK";
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.CREATED);
            } else { //a non logged in person wants to send something, it is prohibited
                resultString = "Üzenetküldés sikertelen.";
                logger.warn("WARNING, somebody - who was not logged in - tried to send a message to us.");
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Az üzenetküldés sikertelen, kérjük lépjen kapcsolatba a weboldal karbantartójával!";
            logger.warn("Error happened at send message to coordinator function, pls contact to maintainers", e);
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

}
