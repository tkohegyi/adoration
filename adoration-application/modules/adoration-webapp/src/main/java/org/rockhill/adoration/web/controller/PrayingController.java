package org.rockhill.adoration.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling requests for the application pages about praying.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class PrayingController {

    /**
     * Serves requests for general monthly praying day.
     *
     * @return the name of the jsp to display as result
     */
    @GetMapping(value = "/adoration/prayingDay")
    public String prayingDayPage() {
        return "prayingDay";
    }

}
