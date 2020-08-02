package org.rockhill.adorApp.web.controller;

import com.google.gson.Gson;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.exception.SystemException;
import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.json.DeleteEntityJson;
import org.rockhill.adorApp.web.json.PersonInformationJson;
import org.rockhill.adorApp.web.json.TableDataInformationJson;
import org.rockhill.adorApp.web.provider.*;
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
    @RequestMapping(value = "/adorationSecure/getLinkTable", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getLinkTable(HttpSession httpSession, @RequestParam("filter") Optional<String> filter) {
        TableDataInformationJson content = null;
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            //can get the link table
            Object o = linkProvider.getLinkListAsObject(currentUserProvider.getUserInformation(httpSession));
            content = new TableDataInformationJson(o);
        }
        return content;
    }

}
