package org.rockhill.adoration.web.controller;

import com.google.gson.Gson;
import org.rockhill.adoration.database.tables.Social;
import org.rockhill.adoration.exception.SystemException;
import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.rockhill.adoration.web.json.DeleteEntityJson;
import org.rockhill.adoration.web.json.TableDataInformationJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.LogFileProvider;
import org.rockhill.adoration.web.provider.SocialProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * Controller for accessing the application log files.
 */
@Controller
public class SocialController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(SocialController.class);

    @Autowired
    private LogFileProvider logFileProvider;
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private SocialProvider socialProvider;

    /**
     * Serves the social page.
     *
     * @return the name of the social jsp file
     */
    @RequestMapping(value = "/adorationSecure/social", method = RequestMethod.GET)
    public String social(HttpSession httpSession) {
        if (!isAdoratorAdmin(currentUserProvider, httpSession)) {
            return "redirect:/adoration/";
        }
        return "social";
    }


    /**
     * Gets the list of Socials
     *
     * @return with the list of socials as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getSocialTable", method = {RequestMethod.GET})
    public TableDataInformationJson getSocialTable(HttpSession httpSession) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person table
            Object people = socialProvider.getSocialListAsObject(); // this says [{"id":372,"name" we need data in head
            content = new TableDataInformationJson(people);
        }
        return content;
    }

    /**
     * Gets specific Social
     *
     * @return with the social as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getSocial/{id:.+}", method = {RequestMethod.GET})
    public TableDataInformationJson getSocialById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the person
            Long id = Long.valueOf(requestedId);
            Object social = socialProvider.getSocialAsObject(id);
            content = new TableDataInformationJson(social);
        }
        return content;
    }

    /**
     * Update an existing Social.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/updateSocial", method = RequestMethod.POST)
    public ResponseEntity<String> updateSocial(@RequestBody final String body, final HttpSession session) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            Social p = g.fromJson(body, Social.class);
            //check authorization: user must have right user type
            if (!currentUserInformationJson.isAdoratorAdmin) {
                resultString = "Unauthorized action.";
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //authorization checked, ok
                Long updateInformation = socialProvider.updateSocial(p, currentUserInformationJson);
                if (updateInformation != null) {
                    resultString = "OK-" + updateInformation.toString();
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot update the Social, please check the values and retry.";
                    logger.info("Cannot update the Social with ID:" + p.getId());
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot update the Social, please contact to maintainers.";
            logger.warn("Error happened at Social, pls contact to maintainers", e);
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Gets log history of a specific Social
     *
     * @return with the social history as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getSocialHistory/{id:.+}", method = {RequestMethod.GET})
    public TableDataInformationJson getSocialHistoryById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the social history
            Long id = Long.valueOf(requestedId);
            Object socialHistory = socialProvider.getSocialHistoryAsObject(id);
            content = new TableDataInformationJson(socialHistory);
        }
        return content;
    }

    /**
     * Delete an existing Social.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/deleteSocial", method = RequestMethod.POST)
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
                Long updatedObjectId = socialProvider.deleteSocial(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot delete Social, please check and retry.";
                    logger.info("Cannot delete Link - data issue.");
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.warn("Error happened at delete Social call", e);
            resultString = "Cannot delete Social, pls contact to maintainers.";
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

}
