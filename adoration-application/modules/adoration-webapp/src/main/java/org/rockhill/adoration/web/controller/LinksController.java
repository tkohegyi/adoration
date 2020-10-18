package org.rockhill.adoration.web.controller;

import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.TableDataInformationJson;
import org.rockhill.adoration.web.provider.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Controller for accessing the hour assignments.
 */
@Controller
public class LinksController extends ControllerBase {

    private final Logger logger = LoggerFactory.getLogger(LinksController.class);

    @Autowired
    private LogFileProvider logFileProvider;
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private LinkProvider linkProvider;
    @Autowired
    private CoverageProvider coverageProvider;

    /**
     * Serves the links page.
     *
     * @return the name of the adorators jsp file
     */
    @RequestMapping(value = "/adorationSecure/links", method = RequestMethod.GET)
    public String adorators(HttpSession httpSession) {
        if (!isAdoratorAdmin(currentUserProvider, httpSession)) {
            return "redirect:/adoration/";
        }
        return "links";
    }


    /**
     * Gets the list of Adorators
     *
     * @return with the list of people as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getLinkTable", method = {RequestMethod.GET})
    public TableDataInformationJson getLinkTable(HttpSession httpSession, @RequestParam("filter") Optional<String> filter) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the link table
            Object o = linkProvider.getLinkListAsObject(currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(o);
        }
        return content;
    }

    /**
     * Gets log history of a specific link
     *
     * @return with the link history as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getLinkHistory/{id:.+}", method = {RequestMethod.GET})
    public TableDataInformationJson getLinkHistoryById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the history
            Long id = Long.valueOf(requestedId);
            Object history = linkProvider.getLinkHistoryAsObject(id);
            content = new TableDataInformationJson(history);
        }
        return content;
    }

    /**
     * Gets specific Link
     *
     * @return with the link as a JSON response
     */
    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getLink/{id:.+}", method = {RequestMethod.GET})
    public TableDataInformationJson getLinkById(HttpSession httpSession, @PathVariable("id") final String requestedId) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the link
            Long id = Long.valueOf(requestedId);
            Object person = linkProvider.getLinkAsObject(id, currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(person);
        }
        return content;
    }

}
