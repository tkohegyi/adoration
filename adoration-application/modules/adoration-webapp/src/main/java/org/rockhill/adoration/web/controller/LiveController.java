package org.rockhill.adoration.web.controller;

import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.LiveAdoratorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the online adoration.
 */
@Controller
public class LiveController extends ControllerBase {
    private static final String JSON_INFO = "hash";

    @Autowired
    LiveAdoratorProvider liveAdoratorProvider;
    @Autowired
    CurrentUserProvider currentUserProvider;

    /**
     * Serves the applog page.
     *
     * @return the name of the applog jsp file
     */
    @RequestMapping(value = "/adorationSecure/live", method = RequestMethod.GET)
    public String live() {
        return "live";
    }

    @ResponseBody
    @RequestMapping(value = "/adoration/registerLiveAdorator", method = {RequestMethod.GET})
    public Map<String, Collection<String>> registerLiveAdorator(HttpSession httpSession, HttpServletResponse httpServletResponse) {

        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");

        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        String hash = liveAdoratorProvider.registerLiveAdorator(currentUserInformationJson);

        jsonString.add(hash);
        jsonResponse.put(JSON_INFO, jsonString);
        return jsonResponse;
    }

    /**
     * Gets the content of the log file.
     *
     * @param hashString is the adorator identifier
     * @return nothing
     */
    @RequestMapping(value = "/adoration/liveAdorator/{hash:.+}", method = {RequestMethod.GET})
    public ResponseEntity<String> getLogFileContent(HttpSession httpSession, @PathVariable("hash") final String hashString) {
        currentUserProvider.getUserInformation(httpSession); //keep session alive even if user does nothing - after all the user is adorating
        liveAdoratorProvider.incomingTick(hashString);
        String jsonData = "{\"hash\":\"" + hashString + "\"}";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(jsonData, responseHeaders, HttpStatus.OK);
    }

}
