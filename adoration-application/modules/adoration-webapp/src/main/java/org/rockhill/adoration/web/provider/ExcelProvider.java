package org.rockhill.adoration.web.provider;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.rockhill.adoration.database.business.BusinessWithCoordinator;
import org.rockhill.adoration.database.business.BusinessWithLink;
import org.rockhill.adoration.database.business.BusinessWithPerson;
import org.rockhill.adoration.database.business.BusinessWithTranslator;
import org.rockhill.adoration.database.business.helper.enums.AdorationMethodTypes;
import org.rockhill.adoration.database.business.helper.enums.AdoratorStatusTypes;
import org.rockhill.adoration.database.tables.Coordinator;
import org.rockhill.adoration.database.tables.Link;
import org.rockhill.adoration.database.tables.Person;
import org.rockhill.adoration.web.configuration.PropertyDTO;
import org.rockhill.adoration.web.configuration.WebAppConfigurationAccess;
import org.rockhill.adoration.web.json.CoverageInformationJson;
import org.rockhill.adoration.web.json.CurrentUserInformationJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.ServletOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelProvider {

    private static final String THERE_IS_NO_INFORMATION = "-";
    public static HashMap<Integer, String> hourCodes;
    public static HashMap<Integer, String> dayCodes;

    static {
        hourCodes = new HashMap<>();
        hourCodes.put(0, "V;");
        hourCodes.put(1, "H;");
        hourCodes.put(2, "K;");
        hourCodes.put(3, "Sze;");
        hourCodes.put(4, "Cs;");
        hourCodes.put(5, "P;");
        hourCodes.put(6, "Szo;");
        dayCodes = new HashMap<>();
        dayCodes.put(0, "vasárnap");
        dayCodes.put(1, "hétfő");
        dayCodes.put(2, "kedd");
        dayCodes.put(3, "szerda");
        dayCodes.put(4, "csütörtök");
        dayCodes.put(5, "péntek");
        dayCodes.put(6, "szombat");
    }

    @Autowired
    BusinessWithPerson businessWithPerson;
    @Autowired
    BusinessWithLink businessWithLink;
    @Autowired
    BusinessWithCoordinator businessWithCoordinator;
    @Autowired
    WebAppConfigurationAccess webAppConfigurationAccess;
    @Autowired
    CoverageProvider coverageProvider;
    @Autowired
    CoordinatorProvider coordinatorProvider;
    @Autowired
    BusinessWithTranslator businessWithTranslator;

    private Row getRow(Sheet sheet, int rowNo) {
        Row row = sheet.getRow(rowNo);
        if (row == null) {
            row = sheet.createRow(rowNo);
        }
        return row;
    }

    private Cell getCell(Row row, int colNo) {
        Cell cell = row.getCell(colNo);
        if (cell == null) {
            cell = row.createCell(colNo);
        }
        return cell;
    }

    private Cell getSheetCell(Sheet sheet, int rowNo, int colNo) {
        Row row;
        Cell cell;
        row = getRow(sheet, rowNo);
        cell = getCell(row, colNo);
        return cell;
    }

    private void setCellOnSheet(Sheet sheet, int rowNo, int colNo, String cellValue) {
        getSheetCell(sheet, rowNo, colNo).setCellValue(cellValue);
    }

    private void reCalculateAllExcelCells(Workbook w) {
        XSSFFormulaEvaluator.evaluateAllFormulaCells(w); //NOSONAR - this is an external lib call, we have to use this
    }

    private Workbook getSpecificXlsTemplate(final String excelFilename) throws IOException {
        Workbook workbook;
        try (FileInputStream file = new FileInputStream(new File(excelFilename))) {
            workbook = new XSSFWorkbook(file);
        }
        return workbook;
    }

    public void getExcelFull(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialXls();
        if (w != null) {
            updateAdorators(w);
            updateCoverage(userInformation, w);
            reCalculateAllExcelCells(w);
            w.write(outputStream);
        }
    }

    private Workbook createInitialXls() throws IOException {
        PropertyDTO propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getExcelFileName());
    }

    private String getHourString(Integer hourId) {
        Integer day = hourId / 24;
        int hour = hourId % 24;
        String dayString = hourCodes.get(day);
        return dayString + hour + ";";
    }

    private void fillCellsWithLinks(List<Link> links, Cell cellOfRow14, Cell cellOfRow15) {
        StringBuilder physical = new StringBuilder();
        StringBuilder online = new StringBuilder();
        boolean wasP = false;
        boolean wasO = false;
        for (Link l : links) {
            String hString = getHourString(l.getHourId()) + l.getPriority().toString();
            if (l.getType().equals(AdorationMethodTypes.PHYSICAL.getAdorationMethodValue())) {
                //physical
                if (wasP) {
                    physical.append(" ");
                }
                wasP = true;
                physical.append(hString);
            } else {
                //online
                if (wasO) {
                    online.append(" ");
                }
                wasO = true;
                online.append(hString);
            }
        }
        cellOfRow14.setCellValue(physical.toString());
        cellOfRow15.setCellValue(online.toString());
    }

    private void updateAdorators(Workbook w) {
        Sheet sheet = w.getSheet("Adorálók");
        int rowCount = 1;
        List<Person> people = businessWithPerson.getPersonList();
        for (Person p : people) {
            Row row = getRow(sheet, rowCount);
            Cell c = getCell(row, 0);
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
                fillCellsWithLinks(links, row.createCell(14), row.createCell(15));
            }
            rowCount++;
        }
    }

    private void updateCoverage(CurrentUserInformationJson userInformation, Workbook w) {
        CoverageInformationJson coverageInformationJson = coverageProvider.getCoverageInfo(userInformation);
        Sheet sheet = w.getSheet("Fedettség");
        //public Map<Integer,Integer> visibleHours; //hourId - number of adorators, only prio 1,2
        int rowCount = 39;
        for (int d = 0; d < 7; d++) {
            Row row = sheet.createRow(rowCount + d);
            for (int h = 0; h < 24; h++) {
                Integer noOfAdorators = coverageInformationJson.visibleHours.get(d * 24 + h);
                Cell c = getCell(row, 4 + h);
                long cellValue = 2 - noOfAdorators.longValue();
                if (cellValue < 0) {
                    cellValue = 0;
                }
                c.setCellValue(cellValue);
                c.setCellType(CellType.NUMERIC);
            }
        }
    }

    public void getExcelDailyInfo(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialDailyInfoXls();
        if (w != null) {
            updateDailyInfo(userInformation, w);
            reCalculateAllExcelCells(w);
            w.write(outputStream);
        }
    }

    private Workbook createInitialDailyInfoXls() throws IOException {
        PropertyDTO propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getDailyInfoFileName());
    }

    private void updateDailyInfo(CurrentUserInformationJson userInformation, Workbook w) {
        //fill hour coordinators
        Sheet sheet = w.getSheet("Adatok");
        fillCellsWithHourlyCoordinators(sheet, 3, 2);
        //prepare data
        Map<Integer, Integer> posRecord = new HashMap<>();
        for (int i = BusinessWithLink.MIN_HOUR; i <= BusinessWithLink.MAX_HOUR; i++) { //this is about priority only
            posRecord.put(i, 0);
        }
        //fill data
        int colBase = 4;
        int rowBase = 3;
        for (int i = BusinessWithLink.MIN_HOUR; i <= BusinessWithLink.MAX_HOUR; i++) {
            List<Link> links = businessWithLink.getPhysicalLinksOfHour(i);
            if (links != null && links.size() > 0) {
                links.sort(Comparator.comparing(Link::getPriority));
                for (Link l : links) {
                    Cell cell = getSheetCell(sheet, rowBase + posRecord.get(i), colBase + i);
                    Long personId = l.getPersonId();
                    if (personId != null) {
                        Person p = businessWithPerson.getPersonById(personId);
                        cell.setCellValue(p.getId().toString() + " - " + p.getName() + "\n" + p.getMobile());
                        posRecord.put(i, posRecord.get(i) + 1);
                    }
                }
            }
        }
    }

    private void fillCellsWithHourlyCoordinators(Sheet sheet, int rowPos, int colPos) {
        List<Coordinator> coordinators = coordinatorProvider.getCoordinatorList();
        for (Coordinator coo : coordinators) {
            if (coo.getCoordinatorType() < 24) { //if hourly coordinator
                Long personId = coo.getPersonId();
                if (personId != null && personId.intValue() > 0) {
                    Person p = businessWithPerson.getPersonById(coo.getPersonId());
                    String cellValue = p.getId().toString() + " - " + p.getName() + "\n" + p.getMobile();
                    setCellOnSheet(sheet, rowPos + coo.getCoordinatorType(), colPos, cellValue);
                }
            }
        }
    }

    public void getExcelHourlyInfo(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialHourlyInfoXls();
        if (w != null) {
            updateHourlyInfo(userInformation, w);
            reCalculateAllExcelCells(w);
            w.write(outputStream);
        }
    }

    private Workbook createInitialHourlyInfoXls() throws IOException {
        PropertyDTO propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getHourlyInfoFileName());
    }

    private void updateHourlyInfo(CurrentUserInformationJson userInformation, Workbook w) {
        Sheet sheet = w.getSheet("Órakoordinátor");
        if (userInformation.personId == null) {
            return;
        } // the person is not identified
        Coordinator coordinator = businessWithCoordinator.getCoordinatorFromPersonId(userInformation.personId);
        if (coordinator == null) {
            return;
        } //the coordinator is not identified
        //coordinator has been identified
        Integer coordinatorType = coordinator.getCoordinatorType();
        if (coordinatorType > 23) {
            return; // the person is not an hourly coordinator
        }
        //hourly coordinator identified, fill the xls
        Cell c = getSheetCell(sheet, 2, 2);
        c.setCellValue(coordinator.getCoordinatorType());
        Coordinator dailyCoo = businessWithCoordinator.getDailyCooOfHour(coordinatorType);
        if (dailyCoo != null) {
            Person p = businessWithPerson.getPersonById(dailyCoo.getPersonId());
            if (p != null) {
                Row row = getRow(sheet, 6); //row of daily coo
                c = getCell(row, 1);
                c.setCellValue(p.getName());
                c = getCell(row, 3);
                c.setCellValue(p.getMobile());
                c = getCell(row, 4);
                c.setCellValue(p.getEmail());
            }
        }
        Person p = businessWithPerson.getPersonById(coordinator.getPersonId());
        if (p != null) { // if we have the hourly coo person
            Row row = getRow(sheet, 10); //hourly coo row
            c = getCell(row, 1);
            c.setCellValue(p.getName());
            c = getCell(row, 3);
            c.setCellValue(p.getMobile());
            c = getCell(row, 4);
            c.setCellValue(p.getEmail());
            c = getCell(row, 5);
            c.setCellValue(p.getVisibleComment());
        }
        //iterate through the adorators
        List<Link> links = businessWithLink.getLinksOfWeek(coordinatorType);
        int baseRow = 14;
        for (Link l : links) {
            Integer hourId = l.getHourId();
            Integer day = hourId / 24;
            String dayString = dayCodes.get(day);
            p = businessWithPerson.getPersonById(l.getPersonId());
            if (p != null) {
                Row row = getRow(sheet, baseRow); //adorator row
                c = getCell(row, 1);
                c.setCellValue(dayString);
                c = getCell(row, 2);
                c.setCellValue(p.getName());
                c = getCell(row, 3);
                c.setCellValue(p.getMobile());
                c = getCell(row, 4);
                c.setCellValue(p.getEmail());
                c = getCell(row, 5);
                c.setCellValue(p.getCoordinatorComment());
                c = getCell(row, 6);
                c.setCellValue(p.getVisibleComment());
                baseRow++;
            }
        }
    }

    public void getExcelAdoratorInfo(CurrentUserInformationJson userInformation, ServletOutputStream outputStream) throws IOException {
        Workbook w = createInitialAdoratorInfoXls();
        if (w != null) {
            updateAdoratorInfo(userInformation, w);
            reCalculateAllExcelCells(w);
            w.write(outputStream);
        }
    }

    private Workbook createInitialAdoratorInfoXls() throws IOException {
        PropertyDTO propertyDto = webAppConfigurationAccess.getProperties();
        return getSpecificXlsTemplate(propertyDto.getAdoratorInfoFileName());
    }

    private void updateAdoratorInfo(CurrentUserInformationJson userInformation, Workbook w) {
        final String publicName = "Publikus";
        final String hiddenName = "Titkos";
        if (userInformation.personId == null) return;
        Sheet sheet = w.getSheet("Adoráló");
        Person p = businessWithPerson.getPersonById(userInformation.personId);
        setCellOnSheet(sheet, 2, 2, p.getName());
        if (!p.getIsAnonymous()) {
            setCellOnSheet(sheet, 2, 3, publicName);
        } else {
            setCellOnSheet(sheet, 2, 3, hiddenName);
        }
        setCellOnSheet(sheet, 3, 2, AdoratorStatusTypes.getTranslatedString(p.getAdorationStatus()));
        setCellOnSheet(sheet, 4, 2, p.getId().toString());
        setCellOnSheet(sheet, 5, 2, p.getMobile());
        if (p.getMobileVisible()) {
            setCellOnSheet(sheet, 5, 3, publicName);
        } else {
            setCellOnSheet(sheet, 5, 3, hiddenName);
        }
        setCellOnSheet(sheet, 6, 2, p.getEmail());
        if (p.getEmailVisible()) {
            setCellOnSheet(sheet, 6, 3, publicName);
        } else {
            setCellOnSheet(sheet, 6, 3, hiddenName);
        }
        setCellOnSheet(sheet, 7, 2, p.getVisibleComment());
        //fill assigned hours
        int baseRow = 10;
        fillCellsWithAssignedHours(sheet, p, baseRow);
        //fill daily coordinators
        baseRow = 20;
        List<Coordinator> allCoordinators = businessWithCoordinator.getLeadership();
        for (Coordinator c : allCoordinators) {
            if (c.getPersonId() != null) {
                Person coo = businessWithPerson.getPersonById(c.getPersonId());
                if (coo != null) {
                    String coordinatorText = businessWithTranslator.getTranslatorValue(userInformation.languageCode,
                            "COORDINATOR-" + c.getCoordinatorType().toString(), "N/A");
                    setCellOnSheet(sheet, baseRow, 1, coordinatorText);
                    setCellOnSheet(sheet, baseRow, 3, getPersonNameInformation(coo));
                    setCellOnSheet(sheet, baseRow, 4, getPersonMobileAndEmailInformation(coo));
                    baseRow++;
                }
            }
        }
        //fill date
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        setCellOnSheet(sheet, 30, 2, dtf.format(now));
    }

    private void fillCellsWithAssignedHours(Sheet sheet, Person p, int baseRow) {
        List<Link> links = businessWithLink.getLinksOfPerson(p);
        if (links != null) {  //have assigned hours to be filled
            for (Link l : links) {
                Integer hourId = l.getHourId();
                int day = hourId / 24;
                Integer hour = hourId - day * 24;
                String dayString = dayCodes.get(day);
                setCellOnSheet(sheet, baseRow, 1, dayString + " " + hour.toString() + " óra");
                Coordinator coo = businessWithCoordinator.getHourlyCooOfHour(hour);
                Person cooP = null;
                if (coo != null) {
                    cooP = businessWithPerson.getPersonById(coo.getPersonId());
                }
                if (cooP != null) {
                    setCellOnSheet(sheet, baseRow, 2, getPersonNameInformation(cooP));
                    setCellOnSheet(sheet, baseRow, 3, getPersonMobileAndEmailInformation(cooP));
                } else {
                    setCellOnSheet(sheet, baseRow, 2, THERE_IS_NO_INFORMATION);
                    setCellOnSheet(sheet, baseRow, 3, THERE_IS_NO_INFORMATION);
                }
                //fill previous hour
                Integer previousHour = businessWithLink.getPreviousHour(hourId);
                fillNeighbourHour(sheet, previousHour, baseRow, 4);
                //fill next hour
                Integer nextHour = businessWithLink.getNextHour(hourId);
                fillNeighbourHour(sheet, previousHour, baseRow, 6);

                baseRow++;
            }
        }
    }

    private void fillNeighbourHour(Sheet sheet, Integer previousHour, int baseRow, int baseCol) {
        List<Link> previousLinks = businessWithLink.getLinksOfHour(previousHour);
        if (previousLinks != null && previousLinks.size() > 0) {
            Link aPreviousLink = previousLinks.get(0);
            Person aPreviousPerson = businessWithPerson.getPersonById(aPreviousLink.getPersonId());
            if (aPreviousPerson != null) {
                setCellOnSheet(sheet, baseRow, baseCol, getPersonNameInformation(aPreviousPerson));
                setCellOnSheet(sheet, baseRow, baseCol + 1, getPersonMobileAndEmailInformation(aPreviousPerson));
            } else {
                setCellOnSheet(sheet, baseRow, baseCol, THERE_IS_NO_INFORMATION);
                setCellOnSheet(sheet, baseRow, baseCol + 1, THERE_IS_NO_INFORMATION);
            }
        }
    }

    private String getPersonNameInformation(Person p) {
        String name;
        if (!p.getIsAnonymous()) {
            name = p.getName();
        } else {
            name = "Anoním";
        }
        return name;
    }

    private String getPersonMobileAndEmailInformation(Person p) {
        String mobile;
        if (p.getMobileVisible()) {
            mobile = p.getMobile();
        } else {
            mobile = THERE_IS_NO_INFORMATION;
        }
        String email;
        if (p.getEmailVisible()) {
            email = p.getEmail();
        } else {
            email = THERE_IS_NO_INFORMATION;
        }
        return "tel: " + mobile + " / e-mail: " + email;
    }

}
