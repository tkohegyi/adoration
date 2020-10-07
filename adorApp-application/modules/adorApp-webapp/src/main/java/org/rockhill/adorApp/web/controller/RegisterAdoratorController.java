package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import org.rockhill.adorApp.exception.SystemException;
import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.rockhill.adorApp.web.json.RegisterAdoratorJson;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
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
 * Controller for registering a new adorator.
 */
@Controller
public class RegisterAdoratorController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(AdoratorsController.class);

    @Autowired
    CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;

    /**
     * Serves the adorRegistration page.
     *
     * @return the name of the adorRegistration jsp file
     */
    @RequestMapping(value = "/adoration/adorRegistration", method = RequestMethod.GET)
    public String adorRegistration(HttpSession httpSession,
                         HttpServletResponse httpServletResponse) {
        return "adorRegistration";
    }

    /**
     * Update an existing Person.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adoration/registerAdorator", method = RequestMethod.POST)
    public ResponseEntity<String> registerAdorator(@RequestBody final String body, final HttpSession session) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            RegisterAdoratorJson p = g.fromJson(body, RegisterAdoratorJson.class);
            p.personId = currentUserInformationJson.personId;
            p.socialId = currentUserInformationJson.socialId;
            //authorization is irrelevant
            Long updateInformation = peopleProvider.registerAdorator(p, currentUserInformationJson);
            if (updateInformation != null) {
                resultString = "OK-" + updateInformation.toString();
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.CREATED);
            } else {
                resultString = "A regisztrálás sikertelen, kérjük ellenőrizze a megadott adatokat és próbálkozzon újra.";
                logger.info("Cannot register Adorator:" + p.name);
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "A regiszrálás sikertelen, kérjük lépjen kapcsolatba a weboldal karbantartójával!";
            logger.warn("Error happened at register new Adorator function, pls contact to maintainers", e);
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    @RequestMapping(value = "/adoration/registrationSuccess", method = RequestMethod.GET)
    public String registrationSuccess(HttpSession httpSession,
                                   HttpServletResponse httpServletResponse) {
        return "registrationSuccess";
    }

}
