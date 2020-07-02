package org.rockhill.adorApp.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for handling requests for the application pages about Information.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class InformationController {
    private final Logger logger = LoggerFactory.getLogger(InformationController.class);

    /**
     * Serves requests for general Information.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adorationSecure/information", method = {RequestMethod.GET, RequestMethod.POST})
    public String prayingDayPage() {
        return "information";
    }

}
