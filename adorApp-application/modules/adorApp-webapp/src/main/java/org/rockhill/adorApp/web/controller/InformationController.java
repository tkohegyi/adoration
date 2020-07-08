package org.rockhill.adorApp.web.controller;

import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.json.TableDataInformationJson;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.rockhill.adorApp.web.provider.InformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Controller for handling requests for the application pages about Information.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class InformationController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(InformationController.class);
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private InformationProvider informationProvider;

    /**
     * Serves requests for general Information.
     *
     * @return the name of the jsp to display as result
     */
    @RequestMapping(value = "/adorationSecure/information", method = {RequestMethod.GET, RequestMethod.POST})
    public String prayingDayPage() {
        return "information";
    }

    @ResponseBody
    @RequestMapping(value = "/adorationSecure/getInformation", method = {RequestMethod.GET, RequestMethod.POST})
    public TableDataInformationJson getInformation(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
        httpServletResponse.setHeader("Pragma", "no-cache");
        TableDataInformationJson content = null;
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            //has right to collect and see information
            Object information = informationProvider.getInformation(currentUserProvider.getUserInformation(httpSession), httpSession);
            content = new TableDataInformationJson(information);
        }
        return content;
    }

}
