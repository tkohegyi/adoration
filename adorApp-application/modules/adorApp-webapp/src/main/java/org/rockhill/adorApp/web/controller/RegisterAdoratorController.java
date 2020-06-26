package org.rockhill.adorApp.web.controller;

import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for registering a new adorator.
 */
@Controller
public class RegisterAdoratorController extends ControllerBase {

    @Autowired
    CurrentUserProvider currentUserProvider;

    /**
     * Serves the adorRegistration page.
     *
     * @return the name of the adorRegistration jsp file
     */
    @RequestMapping(value = "/adoration/adorRegistration", method = RequestMethod.GET)
    public String adorRegistration(HttpSession httpSession,
                         HttpServletResponse httpServletResponse) {
        return "adorRegistration";
    }

}
