package org.rockhill.adoration.web.controller;

import com.google.gson.Gson;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.exception.SystemException;
import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.DeleteEntityJson;
import org.rockhill.adoration.web.json.PersonInformationJson;
import org.rockhill.adoration.web.json.TableDataInformationJson;
import org.rockhill.adoration.web.provider.CoverageProvider;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.LogFileProvider;
import org.rockhill.adoration.web.provider.PeopleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Controller for accessing the application log files.
 */
@Controller
public class AdoratorsController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(AdoratorsController.class);

    @Autowired
    private LogFileProvider logFileProvider;
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;
    @Autowired
    private CoverageProvider coverageProvider;

    /**
     * Serves the adorators page.
     *
     * @return the name of the adorators jsp file
     */
    @RequestMapping(value = "/adorationSecure/adorators", method = RequestMethod.GET)
    public String adorators(HttpSession httpSession) {
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
    @RequestMapping(value = "/adorationSecure/getPersonTable", method = {RequestMethod.GET})
    public TableDataInformationJson getPersonTable(HttpSession httpSession, @RequestParam("filter") Optional<String> filter) {
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
    @RequestMapping(value = "/adorationSecure/getPerson/{id:.+}", method = {RequestMethod.GET})
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
    @RequestMapping(value = "/adorationSecure/getPersonHistory/{id:.+}", method = {RequestMethod.GET})
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
    @RequestMapping(value = "/adorationSecure/getPersonCommitments/{id:.+}", method = {RequestMethod.GET})
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
                    resultString = "Cannot delete Person, please check and retry.";
                    logger.info("Cannot delete Person - data issue.");
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.warn("Error happened at delete Person call", e);
            resultString = "Cannot delete Person, pls contact to maintainers.";
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
