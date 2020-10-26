package org.rockhill.adoration.web.controller;

import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.json.TableDataInformationJson;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.PeopleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * Controller for accessing the adorator information.
 */
@Controller
public class AdoratorListController extends ControllerBase {

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private PeopleProvider peopleProvider;

    /**
     * Serves the adorators page.
     *
     * @return the name of the adorators jsp file
     */
    @GetMapping(value = "/adorationSecure/adorationList")
    public String adorators(HttpSession httpSession) {
        if (!isRegisteredAdorator(currentUserProvider, httpSession)) {
            return "redirect:/adoration/";
        }
        return "adoratorList";
    }

    @ResponseBody
    @GetMapping(value = "/adorationSecure/getAdoratorList")
    public TableDataInformationJson getPersonTable(HttpSession httpSession, @RequestParam("filter") Optional<String> filter) {
        TableDataInformationJson content = null;
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            Object people = peopleProvider.getAdoratorListAsObject(currentUserProvider.getUserInformation(httpSession), isPrivilegedAdorator(currentUserProvider, httpSession));
            content = new TableDataInformationJson(people);
        }
        return content;
    }

}

