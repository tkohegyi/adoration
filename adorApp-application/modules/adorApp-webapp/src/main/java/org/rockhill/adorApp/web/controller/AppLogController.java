package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.rockhill.adorApp.exception.SystemException;
import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.rockhill.adorApp.web.json.TableDataInformationJson;
import org.rockhill.adorApp.web.provider.CoverageProvider;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.rockhill.adorApp.web.provider.LogFileProvider;
import org.rockhill.adorApp.web.provider.PeopleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for accessing the application log files.
 */
@Controller
public class AppLogController extends ControllerBase {

    private static final String JSON_NAME = "files";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";
    private static final String JSON_APP_INFO = "adorAppApplication";
    private final RequestMappingHandlerMapping handlerMapping;
    private final Logger logger = LoggerFactory.getLogger(AppLogController.class);

    @Autowired
    private LogFileProvider logFileProvider;
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;
    @Autowired
    private CoverageProvider coverageProvider;

    @Autowired
    public AppLogController(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Serves the applog page.
     *
     * @return the name of the applog jsp file
     */
    @RequestMapping(value = "/adorationSecure/applog", method = RequestMethod.GET)
    public String applog(HttpSession httpSession,
                         HttpServletResponse httpServletResponse) {
        if (!isAdoratorAdmin(currentUserProvider, httpSession)) {
            return "redirect:/adoration/";
        }
        return "applog";
    }

    /**
     * Gets the list of log files.
     *
     * @return with the list of log files as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/logs", method = {RequestMethod.GET, RequestMethod.POST})
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
    @RequestMapping(value = "/adorationSecure/logs/{fileName:.+}", method = {RequestMethod.GET, RequestMethod.POST})
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

    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getAdorAppServerInfo", method = {RequestMethod.GET})
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

    /**
     * Gets the list of Adorators
     *
     * @return with the list of people as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getPersonTable", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getPersonTable(HttpSession httpSession) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person table
            Object people = peopleProvider.getPersonListAsObject(); // this says [{"id":372,"name" we need data in head
            content = new TableDataInformationJson(people);
        }
        return content;
    }

    /**
     * Gets specific Adorator
     *
     * @return with the person as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getPerson/{id:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getPersonById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person
            Long id = Long.valueOf(requestedId);
            Object person = peopleProvider.getPersonAsObject(id);
            content = new TableDataInformationJson(person);
        }
        return content;
    }

    /**
     * Update an existing Person.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/updatePerson", method = RequestMethod.POST)
    public ResponseEntity<String> updatePerson(@RequestBody final String body, final HttpSession session) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            PersonInformationJson p = g.fromJson(body, PersonInformationJson.class);
            //check authorization: user must have right user type
            if (!currentUserInformationJson.isAdoratorAdmin) {
                resultString = "Unauthorized action.";
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //authorization checked, ok
                Long updateInformation = peopleProvider.updatePerson(p, currentUserInformationJson);
                if (updateInformation != null) {
                    resultString = "OK-" + updateInformation.toString();
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot update the Person, please check the values and retry.";
                    logger.info("Cannot update the Person with ID:" + p.id);
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot update the Person, please contact to maintainers.";
            logger.warn("Error happened at Person, pls contact to maintainers", e);
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Gets log history of a specific Adorator
     *
     * @return with the person history as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getPersonHistory/{id:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getPersonHistoryById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person history
            Long id = Long.valueOf(requestedId);
            Object personHistory = peopleProvider.getPersonHistoryAsObject(id);
            content = new TableDataInformationJson(personHistory);
        }
        return content;
    }

    /**
     * Gets hour assignments of a specific Adorator
     *
     * @return with the hour assignments of a person as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getPersonCommitments/{id:.+}", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getPersonCommitmentsById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person commitments
            Long id = Long.valueOf(requestedId);
            Object personCommitments = coverageProvider.getPersonCommitmentAsObject(id);
            content = new TableDataInformationJson(personCommitments);
        }
        return content;
    }

}
