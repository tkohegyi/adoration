package org.rockhill.adorApp.web.controller;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.provider.LiveAdoratorProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
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

    /**
     * Serves the applog page.
     *
     * @return the name of the applog jsp file
     */
    @RequestMapping(value = "/adorationSecure/live", method = RequestMethod.GET)
    public String live(HttpSession httpSession,
                         HttpServletResponse httpServletResponse) {
        return "live";
    }

    @ResponseBody
    @RequestMapping(value = "/adoration/registerLiveAdorator", method = {RequestMethod.GET})
    public Map<String, Collection<String>> registerLiveAdorator(HttpSession httpSession, HttpServletResponse httpServletResponse) throws IOException {

        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");

        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        Collection<String> jsonString = new ArrayList<>();

        String hash = liveAdoratorProvider.registerLiveAdorator();

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
    public ResponseEntity<String> getLogFileContent(@PathVariable("hash") final String hashString) {
        liveAdoratorProvider.incomingTick(hashString);
        String jsonData = "{\"hash\":\"" + hashString + "\"}";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(jsonData, responseHeaders, HttpStatus.OK);
    }

}
