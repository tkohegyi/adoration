package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import org.rockhill.adorApp.exception.SystemException;
import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.json.*;
import org.rockhill.adorApp.web.provider.CoordinatorProvider;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.rockhill.adorApp.web.provider.InformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests for the application pages about Coordinators.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class CoordinatorController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(CoordinatorController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private CoordinatorProvider coordinatorProvider;

    /**
     * Serves requests for general Coordinator info.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adorationSecure/coordinators", method = {RequestMethod.GET})
    public String coordinators() {
        return "coordinators";
    }

    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getCoordinators", method = {RequestMethod.GET})
    public TableDataInformationJson getCoordinators(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        TableDataInformationJson content = null;
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            //has right to collect and see information
            Object coordinators = coordinatorProvider.getCoordinatorListAsObject(currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(coordinators);
        }
        return content;
    }

    /**
     * Gets specific coordinator
     *
     * @return with the coordinator as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getCoordinator/{id:.+}", method = {RequestMethod.GET})
    public TableDataInformationJson getCoordinatorById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the coordinator
            Long id = Long.valueOf(requestedId);
            Object coordinatorJson = coordinatorProvider.getCoordinatorAsObject(id, currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(coordinatorJson);
        }
        return content;
    }

    /**
     * Update an existing coordinator.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/updateCoordinator", method = RequestMethod.POST)
    public ResponseEntity<String> updateCoordinator(@RequestBody final String body, final HttpSession session) {
        String resultString = "OK";
        ResponseEntity<String> result;
        HttpHeaders responseHeaders = setHeadersForJSON();
        try {
            CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(session);
            Gson g = new Gson();
            CoordinatorJson p = g.fromJson(body, CoordinatorJson.class);
            //check authorization: user must have right user type
            if (!currentUserInformationJson.isAdoratorAdmin) {
                resultString = "Unauthorized action.";
                result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.FORBIDDEN);
            } else {
                //authorization checked, ok
                Long updateInformation = coordinatorProvider.updateCoordinator(p, currentUserInformationJson);
                if (updateInformation != null) {
                    resultString = "OK-" + updateInformation.toString();
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot update the Coordinator, please check the values and retry.";
                    logger.info("Cannot update the Coordinator with ID:" + p.id);
                    result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            resultString = "Cannot update the Coordinator, please contact to maintainers.";
            logger.warn("Error happened at Coordinator, pls contact to maintainers", e);
            result = new ResponseEntity<String>(getJsonString(JSON_RESPONSE_UPDATE, resultString), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

    /**
     * Delete an existing coordinator.
     *
     * @param session is the actual HTTP session
     * @return list of hits as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/deleteCoordinator", method = RequestMethod.POST)
    public ResponseEntity<String> deleteCoordinator(@RequestBody final String body, final HttpSession session) {
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
                Long updatedObjectId = coordinatorProvider.deleteCoordinator(p, currentUserInformationJson);
                if (updatedObjectId != null) {
                    resultString = "OK";
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.CREATED);
                } else {
                    resultString = "Cannot delete Coordinator, please check and retry.";
                    logger.info("Cannot delete Coordinator - data issue.");
                    result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
                }
            }
        } catch (SystemException e) {
            resultString = e.getMessage();
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.warn("Error happened at delete Coordinator call", e);
            resultString = "Cannot delete Coordinator, pls contact to maintainers.";
            result = new ResponseEntity<String>(getJsonString(resultString, JSON_RESPONSE_DELETE), responseHeaders, HttpStatus.BAD_REQUEST);
        }
        return result;
    }

}
