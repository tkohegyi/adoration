package org.rockhill.adorApp.web.controller;

import org.rockhill.adorApp.web.controller.helper.ControllerBase;
import org.rockhill.adorApp.web.provider.CurrentUserProvider;
import org.rockhill.adorApp.web.provider.ExcelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

/**
 * Controller for handling requests for the application pages about Exporting data.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class ExportController extends ControllerBase {
    private final Logger logger = LoggerFactory.getLogger(ExportController.class);
    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private ExcelProvider excelProvider;

    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";

    /**
     * Serves request to get full export to Excel.
     *
     * @return the with the excel file
     */
    @RequestMapping(value = "/adorationSecure/getExcelFull", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<String> getLogFileContent(@PathVariable("fileName") final String fileName,
                                                    @RequestParam(value = "source", defaultValue = "false") final boolean source,
                                                    @RequestHeader(value = "User-Agent", defaultValue = "") final String userAgent) {
        String body = excelProvider.getExcelFull();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        if (!source) {
            headers.set(CONTENT_DISPOSITION, String.format(ATTACHMENT_TEMPLATE, fileName));
        }
        ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, headers, HttpStatus.OK);
        return responseEntity;
    }

}
