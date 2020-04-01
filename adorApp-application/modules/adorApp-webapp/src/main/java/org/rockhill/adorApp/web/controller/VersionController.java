package org.rockhill.adorApp.web.controller;

import org.rockhill.adorApp.configuration.VersionTitleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for version information.
 */
@Controller
public class VersionController {

    @Autowired
    private VersionTitleProvider titleProvider;

    /**
     * Returns the build version of the application as a JSON response.
     *
     * @return the JSON response containing the build version
     */
    @ResponseBody
    @RequestMapping(value = "/version", method = RequestMethod.GET)
    public ResponseEntity<String> getVersion() {
        String adorAppVersion = titleProvider.getVersionTitle();
        String jsonData = "{\"adorAppVersion\":\"" + adorAppVersion + "\"}";
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(jsonData, responseHeaders, HttpStatus.OK);
    }
}
