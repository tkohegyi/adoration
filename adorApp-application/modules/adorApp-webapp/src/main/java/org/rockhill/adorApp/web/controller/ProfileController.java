package org.rockhill.adorApp.web.controller;

import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests for the application pages about logged-in User profile.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class ProfileController {

    @Autowired
    private CurrentUserProvider currentUserProvider;

    /**
     * Serves requests for general Information.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adorationSecure/profile", method = {RequestMethod.GET, RequestMethod.POST})
    public String profilePage(HttpSession httpSession) {
        CurrentUserInformationJson currentUserInformationJson = currentUserProvider.getUserInformation(httpSession);
        if (currentUserInformationJson.isRegisteredAdorator) { //registered adorator, must offer self data update
            return "information"; //TODO
        }
        if (currentUserInformationJson.isLoggedIn) { //can be waiting for identification or guest
            return "information"; //TODO
        }
        return "redirect:/adoration/"; //not even logged in -> go back to basic home page
    }

}
