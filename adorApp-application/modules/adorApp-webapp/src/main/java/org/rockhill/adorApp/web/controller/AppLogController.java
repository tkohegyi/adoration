package org.rockhill.adorApp.web.controller;


import org.rockhill.adorApp.web.provider.LogFileProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for accessing the application log files.
 */
@Controller
public class AppLogController {

    private static final String JSON_NAME = "files";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";
    private final RequestMappingHandlerMapping handlerMapping;
    @Autowired
    private LogFileProvider logFileProvider;

    @Autowired
    public AppLogController(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Serves the applog page.
     *
     * @return the name of the applog jsp file
     */
    @RequestMapping(value = "/applog", method = RequestMethod.GET)
    public String applog() {
        return "applog";
    }

    /**
     * Gets the list of log files.
     *
     * @return with the list of log files as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/logs", method = {RequestMethod.GET, RequestMethod.POST})
    public Map<String, Collection<String>> getLogFiles() {
        Map<String, Collection<String>> jsonResponse = new HashMap<>();
        jsonResponse.put(JSON_NAME, logFileProvider.getLogFileNames());
        return jsonResponse;
    }

    /**
     * Gets the content of the log file.
     *
     * @param fileName  the name of the log file
     * @param source    true if the content should be written directly, false for attachment
     * @param userAgent the User-Agent of the request header
     * @return the content of the log file
     */
    @RequestMapping(value = "/logs/{fileName:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getLogFileContent(@PathVariable("fileName") final String fileName,
                                                    @RequestParam(value = "source", defaultValue = "false") final boolean source,
                                                    @RequestHeader(value = "User-Agent", defaultValue = "") final String userAgent) {
        String body = logFileProvider.getLogContent(fileName);
        body = convertLineBreaksIfOnWindows(body, userAgent);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        if (!source) {
            headers.set(CONTENT_DISPOSITION, String.format(ATTACHMENT_TEMPLATE, fileName));
        }
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, headers, HttpStatus.OK);
        return responseEntity;
    }

    private String convertLineBreaksIfOnWindows(final String body, final String userAgent) {
        String result = body;
        if (userIsOnWindows(userAgent)) {
            result = body.replace("\r", "").replace("\n", "\r\n");
        }
        return result;
    }

    private boolean userIsOnWindows(final String userAgent) {
        return userAgent.toLowerCase().contains("windows");
    }

    @ResponseBody
    @RequestMapping(value = "/adoration/endpointdoc", method = RequestMethod.GET)
    public Map<String, Collection<String>> show() {
        Map<RequestMappingInfo, HandlerMethod> methods = this.handlerMapping.getHandlerMethods();
        Map<String, Collection<String>> jsonResponse = new HashMap<>();

        for (RequestMappingInfo requestMappingInfo : methods.keySet()) {
            Collection<String> collection = new ArrayList<>();
            collection.add(methods.get(requestMappingInfo).toString());
            jsonResponse.put(requestMappingInfo.toString(), collection);
        }

        return jsonResponse;
    }

}
