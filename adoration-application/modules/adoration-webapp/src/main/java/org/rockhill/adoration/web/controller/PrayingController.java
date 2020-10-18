package org.rockhill.adoration.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Controller for handling requests for the application pages about praying.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class PrayingController {
    private final Logger logger = LoggerFactory.getLogger(PrayingController.class);

    /**
     * Serves requests for general monthly praying day.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adoration/prayingDay", method = {RequestMethod.GET})
    public String prayingDayPage() {
        return "prayingDay";
    }

}
