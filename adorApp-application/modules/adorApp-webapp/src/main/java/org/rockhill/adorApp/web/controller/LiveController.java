package org.rockhill.adorApp.web.controller;

import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for the online adoration.
 */
@Controller
public class LiveController extends ControllerBase {

    /**
     * Serves the applog page.
     *
     * @return the name of the applog jsp file
     */
    @RequestMapping(value = "/adorationSecure/live", method = RequestMethod.GET)
    public String live(HttpSession httpSession,
                         HttpServletResponse httpServletResponse) {
        return "live";
    }

}
