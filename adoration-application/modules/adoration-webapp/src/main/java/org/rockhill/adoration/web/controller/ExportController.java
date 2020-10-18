package org.rockhill.adoration.web.controller;

import org.apache.http.HttpStatus;
import org.rockhill.adoration.web.controller.helper.ControllerBase;
import org.rockhill.adoration.web.provider.CurrentUserProvider;
import org.rockhill.adoration.web.provider.ExcelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

/**
 * Controller for handling requests for the application pages about Exporting data.
 *
 * @author Tamas Kohegyi
 */
@Controller
public class ExportController extends ControllerBase {
    private static final String ATTACHMENT_TEMPLATE = "attachment; filename=%s";

    private final Logger logger = LoggerFactory.getLogger(ExportController.class);

    @Autowired
    private CurrentUserProvider currentUserProvider;
    @Autowired
    private ExcelProvider excelProvider;

    /**
     * Serves request to get full export to Excel.
     *
     * @return the with the excel file
     */
    @RequestMapping(value = "/adorationSecure/getExcelFull", method = {RequestMethod.GET})
    public void getExcelContent(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.addHeader(CONTENT_DISPOSITION, String.format(ATTACHMENT_TEMPLATE, "nagyRegiszter.xlsx"));
        httpServletResponse.addHeader(CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        if (isAdoratorAdmin(currentUserProvider, httpSession)) {
            try {
                httpServletResponse.setStatus(200);
                excelProvider.getExcelFull(currentUserProvider.getUserInformation(httpSession), httpServletResponse.getOutputStream());
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue at full xls export.", e);
            }
        } else {
            try {
                httpServletResponse.setStatus(HttpStatus.SC_FORBIDDEN);
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue/b at full xls export.", e);
            }
        }
    }

    /**
     * Serves request to get full export to Excel.
     *
     * @return the with the excel file
     */
    @RequestMapping(value = "/adorationSecure/getExcelDailyInfo", method = {RequestMethod.GET})
    public void getExcelDailyInfo(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.addHeader(CONTENT_DISPOSITION, String.format(ATTACHMENT_TEMPLATE, "napszakFedettség.xlsx"));
        httpServletResponse.addHeader(CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        if (isPrivilegedAdorator(currentUserProvider, httpSession)) {
            try {
                httpServletResponse.setStatus(200);
                excelProvider.getExcelDailyInfo(currentUserProvider.getUserInformation(httpSession), httpServletResponse.getOutputStream());
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue at daily info export.", e);
            }
        } else {
            try {
                httpServletResponse.setStatus(HttpStatus.SC_FORBIDDEN);
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue/b at daily info export.", e);
            }
        }
    }

    /**
     * Serves request to get hourly coordinator info.
     *
     * @return the with the excel file
     */
    @RequestMapping(value = "/adorationSecure/getExcelHourlyInfo", method = {RequestMethod.GET})
    public void getExcelHourlyInfo(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.addHeader(CONTENT_DISPOSITION, String.format(ATTACHMENT_TEMPLATE, "órainformáció.xlsx"));
        httpServletResponse.addHeader(CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        if (isPrivilegedAdorator(currentUserProvider, httpSession)) {
            try {
                httpServletResponse.setStatus(200);
                excelProvider.getExcelHourlyInfo(currentUserProvider.getUserInformation(httpSession), httpServletResponse.getOutputStream());
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue at hourly info export.", e);
            }
        } else {
            try {
                httpServletResponse.setStatus(HttpStatus.SC_FORBIDDEN);
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue/b at hourly info export.", e);
            }
        }
    }

    /**
     * Serves request to get adorator info.
     *
     * @return the with the excel file
     */
    @RequestMapping(value = "/adorationSecure/getExcelAdoratorInfo", method = {RequestMethod.GET})
    public void getExcelAdoratorInfo(HttpSession httpSession, HttpServletResponse httpServletResponse) {
        httpServletResponse.addHeader(CONTENT_DISPOSITION, String.format(ATTACHMENT_TEMPLATE, "adoráló-adatok.xlsx"));
        httpServletResponse.addHeader(CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        if (isRegisteredAdorator(currentUserProvider, httpSession)) {
            try {
                httpServletResponse.setStatus(200);
                excelProvider.getExcelAdoratorInfo(currentUserProvider.getUserInformation(httpSession), httpServletResponse.getOutputStream());
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue at adorator info export.", e);
            }
        } else {
            try {
                httpServletResponse.setStatus(HttpStatus.SC_FORBIDDEN);
                httpServletResponse.flushBuffer();
            } catch (IOException e) {
                logger.warn("Issue/b at adorator info export.", e);
            }
        }
    }

}
