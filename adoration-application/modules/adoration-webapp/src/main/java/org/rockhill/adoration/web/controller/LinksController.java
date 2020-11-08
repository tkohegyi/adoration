package org.rockhill.adoration.web.controller;

import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.TableDataInformationJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.LinkProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Controller for accessing the hour assignments.
 */
@Controller
public class LinksController extends ControllerBase {

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private LinkProvider linkProvider;

    /**
     * Serves the links page.
     *
     * @return the name of the adorators jsp file
     */
    @GetMapping(value = "/adorationSecure/links")
    public String adorators(HttpSession httpSession) {
        if (!isAdoratorAdmin(currentUserProvider, httpSession)) {
            return REDIRECT_TO_HOME;
        }
        return "links";
    }

    /**
     * Gets the list of Links.
     *
     * @return with the list of people as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getLinkTable")
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
     * Gets log history of a specific link.
     *
     * @return with the link history as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getLinkHistory/{id:.+}")
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
     * Gets specific Link.
     *
     * @return with the link as a JSON response
     */
    @ResponseBody
    @GetMapping(value = "/adorationSecure/getLink/{id:.+}")
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
