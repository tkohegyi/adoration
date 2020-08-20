package org.rockhill.adorApp.web.provider;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rockhill.adorApp.database.business.BusinessWithLink;
import org.rockhill.adorApp.database.business.BusinessWithPerson;
import org.rockhill.adorApp.database.business.helper.enums.AdorationMethodTypes;
import org.rockhill.adorApp.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adorApp.database.tables.Coordinator;
import org.rockhill.adorApp.database.tables.Link;
import org.rockhill.adorApp.database.tables.Person;
import org.rockhill.adorApp.web.configuration.PropertyDto;
import org.rockhill.adorApp.web.configuration.WebAppConfigurationAccess;
import org.rockhill.adorApp.web.json.CoverageInformationJson;
import org.rockhill.adorApp.web.json.CurrentUserInformationJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelProvider {

    public static HashMap<Integer, String> hourCodes;
    static {
        hourCodes = new HashMap<>();
        hourCodes.put(0, "V;");
        hourCodes.put(1, "H;");
        hourCodes.put(2, "K;");
        hourCodes.put(3, "Sze;");
        hourCodes.put(4, "Cs;");
        hourCodes.put(5, "P;");
        hourCodes.put(6, "Szo;");
    }

    @Autowired
    BusinessWithPerson businessWithPerson;
    @Autowired
    BusinessWithLink businessWithLink;
    @Autowired
    WebAppConfigurationAccess webAppConfigurationAccess;
    @Autowired
    CoverageProvider coverageProvider;
    @Autowired
    CoordinatorProvider coordinatorProvider;

    public void getExcelFull(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialXls();
        updateAdorators(w);
        updateCoverage(userInformation, w);
        XSSFFormulaEvaluator.evaluateAllFormulaCells(w);
        w.write(outputStream);
    }

    private Workbook createInitialXls() throws IOException {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        FileInputStream file = new FileInputStream(new File(propertyDto.getExcelFileName()));
        Workbook workbook = new XSSFWorkbook(file);
        return workbook;
    }

    private void updateAdorators(Workbook w) {
        Sheet sheet = w.getSheet("Adorálók");
        int rowCount = 1;
        List<Person> people = businessWithPerson.getPersonList();
        for (Person p: people) {
            Row row = sheet.createRow(rowCount);
            Cell c = row.createCell(0);
            c.setCellValue(p.getId().toString());
            c.setCellType(CellType.NUMERIC);
            row.createCell(1).setCellValue(p.getName());
            row.createCell(2).setCellValue(AdoratorStatusTypes.getTranslatedString(p.getAdorationStatus()));
            row.createCell(3).setCellValue(p.getIsAnonymous());
            row.createCell(4).setCellValue(p.getMobile());
            row.createCell(5).setCellValue(p.getMobileVisible());
            row.createCell(6).setCellValue(p.getEmail());
            row.createCell(7).setCellValue(p.getEmailVisible());
            row.createCell(8).setCellValue(p.getAdminComment());
            row.createCell(9).setCellValue(p.getDhcSigned());
            row.createCell(10).setCellValue(p.getDhcSignedDate());
            row.createCell(11).setCellValue(p.getCoordinatorComment());
            row.createCell(12).setCellValue(p.getVisibleComment());
            row.createCell(13).setCellValue(p.getLanguageCode());
            List<Link> links = businessWithLink.getLinksOfPerson(p);
            if (links == null) {
                row.createCell(14).setCellValue("");
                row.createCell(15).setCellValue("");
            } else {
                String physical = "";
                String online = "";
                boolean wasP = false;
                boolean wasO = false;
                for (Link l: links) {
                    String hString = getHourString(l.getHourId()) + l.getPriority().toString();
                    if (l.getType().equals(AdorationMethodTypes.PHYSICAL.getAdorationMethodValue())) {
                        //physical
                        if (wasP) {
                            physical += " ";
                        }
                        wasP = true;
                        physical += hString;
                    } else {
                        //online
                        if (wasO) {
                            online += " ";
                        }
                        wasO = true;
                        online += hString;
                    }
                }
                row.createCell(14).setCellValue(physical);
                row.createCell(15).setCellValue(online);
            }
            rowCount++;
        }
    }

    private String getHourString(Integer hourId) {
        Integer day = hourId / 24;
        Integer hour = hourId % 24;
        String dayString = hourCodes.get(day);
        return dayString + hour.toString() + ";";
    }

    private void updateCoverage(CurrentUserInformationJson userInformation, Workbook w) {
        CoverageInformationJson coverageInformationJson = coverageProvider.getCoverageInfo(userInformation);
        Sheet sheet = w.getSheet("Fedettség");
        //public Map<Integer,Integer> visibleHours; //hourId - number of adorators, only prio 1,2
        int rowCount = 39;
        for (int d = 0; d < 7 ;d++) {
            Row row = sheet.createRow(rowCount + d);
            for (int h = 0; h < 24; h++) {
                Integer noOfAdorators = coverageInformationJson.visibleHours.get(d*24 + h);
                Cell c = row.createCell(4 + h);
                c.setCellValue(2 - noOfAdorators);
                c.setCellType(CellType.NUMERIC);
            }
        }
    }

    public void getExcelDailyInfo(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialDailyInfoXls();
        updateDailyInfo(userInformation, w);
        XSSFFormulaEvaluator.evaluateAllFormulaCells(w);
        w.write(outputStream);
    }

    private Workbook createInitialDailyInfoXls() throws IOException {
        PropertyDto propertyDto = webAppConfigurationAccess.getProperties();
        FileInputStream file = new FileInputStream(new File(propertyDto.getDailyInfoFileName()));
        Workbook workbook = new XSSFWorkbook(file);
        return workbook;
    }

    private void updateDailyInfo(CurrentUserInformationJson userInformation, Workbook w) {
        //fill hour coordinators
        Sheet sheet = w.getSheet("Adatok");
        int rowPos = 3;
        List<Coordinator> coordinators = coordinatorProvider.getCoordinatorList();
        for (Coordinator coo: coordinators) {
            if (coo.getCoordinatorType() < 24) { //if hourly coordinator
                Long personId = coo.getPersonId();
                if ( personId != null && personId.intValue() > 0) {
                    Person p = businessWithPerson.getPersonById(coo.getPersonId());
                    Row row = sheet.getRow(rowPos + coo.getCoordinatorType());
                    if (row == null) {
                        row = sheet.createRow(rowPos + coo.getCoordinatorType());
                    }
                    Cell c = row.createCell(2);
                    c.setCellValue(p.getId().toString() + " - " + p.getName() + "\n" + p.getMobile());
                }
            }
        }
        //prepare data
        Map<Integer,Integer> posRecord = new HashMap<>();
        for (int i = 0; i<168; i++) { //this is about priority
            posRecord.put(i,0);
        }
        //fill data
        int colBase = 4;
        int rowBase = 3;
        for (int i = 0; i<168; i++) {
            List<Link> links = businessWithLink.getPhysicalLinksOfHour(i);
            if (links != null && links.size() > 0) {
                links.sort(Comparator.comparing(Link::getPriority));
                for (Link l : links) {
                    Row row = sheet.getRow(rowBase + posRecord.get(i));
                    if (row == null) {
                        row = sheet.createRow(rowBase + posRecord.get(i));
                    }
                    Cell cell = row.getCell(colBase + i);
                    if (cell == null) {
                        cell = row.createCell(colBase + i);
                    }
                    Long personId = l.getPersonId();
                    if (personId != null) {
                        Person p = businessWithPerson.getPersonById(personId);
                        cell.setCellValue(p.getId().toString() + " - " + p.getName() + "\n" + p.getMobile());
                        posRecord.put(i,posRecord.get(i) + 1);
                    }
                }
            }
        }
    }

}