package org.rockhill.adoration.web.controller;

import com.google.gson.Gson;
import org.rockhill.adoration.exception.SystemException;
import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.RegisterAdoratorJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.PeopleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for registering a new adorator.
 */
@Controller
public class RegisterAdoratorController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(RegisterAdoratorController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;

    /**
     * Serves the adorRegistration page.
     *
     * @return the name of the adorRegistration jsp file
     */
    @GetMapping(value = "/adoration/adorRegistration")
    public String adorRegistration(HttpSession httpSession,
                                   HttpServletResponse httpServletResponse) {
        return "adorRegistration";
    }

    /**
     * Register a new adorator.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @PostMapping(value = "/adoration/registerAdorator")
    public ResponseEntity<String> registerAdorator(@RequestBody final String body, final HttpSession session) {
        String resultString;
        ResponseEntity<String> result;
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            RegisterAdoratorJson p = g.fromJson(body, RegisterAdoratorJson.class);
            p.personId = currentUserInformationJson.personId;
            p.socialId = currentUserInformationJson.socialId;
            //authorization is irrelevant
            Long updateInformation = peopleProvider.registerAdorator(p, currentUserInformationJson.userName);
            if (updateInformation != null) {
                resultString = "OK-" + updateInformation.toString();
                result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.CREATED);
            } else {
                resultString = "A regisztrálás sikertelen, kérjük ellenőrizze a megadott adatokat és próbálkozzon újra.";
                result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
                logger.info("Cannot register Adorator: {}", p.name);
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "A regiszrálás sikertelen, kérjük lépjen kapcsolatba a weboldal karbantartójával!";
            result = buildResponseBodyResult(JSON_RESPONSE_UPDATE, resultString, HttpStatus.BAD_REQUEST);
            logger.warn("Error happened at register new Adorator function, pls contact to maintainers", e);
        }
        return result;
    }

    /**
     * Called when the registration of a new adorator was successful.
     *
     * @return with the correct page content
     */
    @GetMapping(value = "/adoration/registrationSuccess")
    public String registrationSuccess() {
        return "registrationSuccess";
    }

}
