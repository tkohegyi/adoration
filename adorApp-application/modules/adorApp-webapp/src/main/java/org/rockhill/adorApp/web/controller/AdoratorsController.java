package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.exception.SystemException;
import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
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
public class AdoratorsController extends ControllerBase {

    private static final String JSON_NAME = "files";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";
    private static final String JSON_APP_INFO = "adorAppApplication";
    private final RequestMappingHandlerMapping handlerMapping;
    private final Logger logger = LoggerFactory.getLogger(AdoratorsController.class);

    @Autowired
    private LogFileProvider logFileProvider;
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;
    @Autowired
    private CoverageProvider coverageProvider;

    @Autowired
    public AdoratorsController(RequestMappingHandlerMapping handlerMapping) {
        this.handlerMapping = handlerMapping;
    }

    /**
     * Serves the adorators page.
     *
     * @return the name of the adorators jsp file
     */
    @RequestMapping(value = "/adorationSecure/adorators", method = RequestMethod.GET)
    public String applog(HttpSession httpSession,
                         HttpServletResponse httpServletResponse) {
        if (!isAdoratorAdmin(currentUserProvider, httpSession)) {
            return "redirect:/adoration/";
        }
        return "adorators";
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
            Object personCommitments = coverageProvider.getPersonCommitmentAsObject(id, getLanguageCode(currentUserProvider, httpSession));
            content = new TableDataInformationJson(personCommitments);
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
    @RequestMapping(value = "/adorationSecure/updatePersonCommitment", method = RequestMethod.POST)
    public ResponseEntity<String> updatePersonCommitment(@RequestBody final String body, final HttpSession session) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            Link p = g.fromJson(body, Link.class);
            //check authorization: user must have right user type
            if (!currentUserInformationJson.isAdoratorAdmin) {
                resultString = "Unauthorized action.";
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //authorization checked, ok
                Long updateInformation = coverageProvider.updatePersonCommitment(p, currentUserInformationJson);
                if (updateInformation != null) {
                    resultString = "OK-" + updateInformation.toString();
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot update the Person Commitment, please check the values and retry.";
                    logger.info("Cannot update the Person Commitment with ID:" + p.getId());
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot update the Person Commitment, please contact to maintainers.";
            logger.warn("Error happened at PersonCommitment, pls contact to maintainers", e);
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Delete an existing Person.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/deletePerson", method = RequestMethod.POST)
    public ResponseEntity<String> deletePerson(@RequestBody final String body, final HttpSession session) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            DeleteEntityJson p = g.fromJson(body, DeleteEntityJson.class);
            //check authorization
            if (!currentUserInformationJson.isAdoratorAdmin) {
                resultString = "Unauthorized action.";
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_DELETE, resultString), responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //authorization checked, ok
                Long updatedObjectId = peopleProvider.deletePerson(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot delete Person Commitment, please check and retry.";
                    logger.info("Cannot delete Link - data issue.");
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.warn("Error happened at delete Person Commitment call", e);
            resultString = "Cannot delete Person Commitment, pls contact to maintainers.";
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Delete an existing Link.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/deletePersonCommitment", method = RequestMethod.POST)
    public ResponseEntity<String> deletePersonCommitment(@RequestBody final String body, final HttpSession session) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            DeleteEntityJson p = g.fromJson(body, DeleteEntityJson.class);
            //check authorization
            if (!currentUserInformationJson.isAdoratorAdmin) {
                resultString = "Unauthorized action.";
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_DELETE, resultString), responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //authorization checked, ok
                Long updatedObjectId = coverageProvider.deletePersonCommitment(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot delete Person Commitment, please check and retry.";
                    logger.info("Cannot delete Link - data issue.");
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.warn("Error happened at delete Person Commitment call", e);
            resultString = "Cannot delete Person Commitment, pls contact to maintainers.";
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

}
