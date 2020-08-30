package org.rockhill.adorApp.web.controller;

import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for handling requests for the application pages about logged-in User profile.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class ProfileController extends ControllerBase {

    /**
     * Serves requests for general Information.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adorationSecure/profile", method = {RequestMethod.GET, RequestMethod.POST})
    public String prayingDayPage() {
        return "information";
    }

}
