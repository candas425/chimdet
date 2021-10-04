package ro.utcn.frontend.importer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Constants;
import ro.utcn.backend.backendservices.HolidayService;
import ro.utcn.backend.model.Employee;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.helper.WorklogEmployee;
import ro.utcn.helper.JavaFxComponentsHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;

import static ro.utcn.Constants.*;
import static ro.utcn.helper.GeneralHelper.formatter;

/**
 * Condica de prezenta a angajatilor
 *
 * @author Lucas
 */

@Component
public class ExcelCondicaParser {

    @Autowired
    private HolidayService holidayService;

    private static final int NUMBER_OF_AVAILABLE_ROWS = 40;
    private static final String ORA_VENIRII = "${oraVenirii}";
    private static final String ORA_PLECARII = "${oraPlecarii}";
    private static final String ORE_LUCRATE = "${oreLucrate}";

    public void parseFile(List<WorklogEmployee> worklogEmployeeList, String luna, String anul) throws IOException, GeneralExceptions {
        int numberOfRows = worklogEmployeeList.size();
        int numberOfPages = numberOfRows / NUMBER_OF_AVAILABLE_ROWS + (numberOfRows % NUMBER_OF_AVAILABLE_ROWS > 0 ? 1 : 0);

        int counterAngajat = 0;

        for (int i = 0; i < numberOfPages; i++) {

            try (FileInputStream inputStream = new FileInputStream(Constants.FILES_LOCATION + Constants.CONDICA_FILE)) {

                try (Workbook workbook = new XSSFWorkbook(inputStream)) {

                    Sheet firstSheet = workbook.getSheetAt(FIRST_ELEMENT);
                    for (Row nextRow : firstSheet) {
                        Iterator<Cell> cellIterator = nextRow.cellIterator();

                        while (cellIterator.hasNext()) {
                            Cell cell = cellIterator.next();
                            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                                if (cell.getStringCellValue().contains(LUNA_KEYWORD)) {
                                    String content = cell.getStringCellValue();
                                    content = content.replace(LUNA_KEYWORD, luna);
                                    content = content.replace(ANUL_KEYWORD, anul);
                                    cell.setCellValue(content);
                                } else if (cell.getStringCellValue().equals(NUME_KEYWORD)) {
                                    if (counterAngajat < numberOfRows) {
                                        cell.setCellValue(worklogEmployeeList.get(counterAngajat).getEmployee().getNume());
                                    } else {
                                        cell.setCellValue("");
                                    }
                                } else if (cell.getStringCellValue().equals(DATE_KEYWORD)) {
                                    if (counterAngajat < numberOfRows) {
                                        cell.setCellValue(worklogEmployeeList.get(counterAngajat).getLocalDate().format(formatter));
                                    } else {
                                        cell.setCellValue("");
                                    }
                                } else if (cell.getStringCellValue().equals(ORA_VENIRII)) {
                                    excludeCellFromCondica(counterAngajat, numberOfRows, worklogEmployeeList, cell);
                                } else if (cell.getStringCellValue().equals(ORA_PLECARII)) {
                                    excludeCellFromCondica(counterAngajat, numberOfRows, worklogEmployeeList, cell);
                                } else if (cell.getStringCellValue().equals(ORE_LUCRATE)) {
                                    excludeCellFromCondica(counterAngajat, numberOfRows, worklogEmployeeList, cell);
                                    counterAngajat++;
                                }
                            }
                        }
                    }

                    FileOutputStream outputStream = new FileOutputStream(Constants.FILES_LOCATION + "/condicaResult" + String.valueOf(i) + ".xlsx");
                    workbook.write(outputStream);
                    outputStream.close();

                }
            }
            JavaFxComponentsHelper.printFile(Constants.FILES_LOCATION + "/condicaResult" + String.valueOf(i) + ".xlsx");
        }
    }

    /**
     * Used for excluding different cells from condica
     */
    private void excludeCellFromCondica(int contorAngajati, int numarDeRanduri, List<WorklogEmployee> worklogEmployeeList, Cell cell) {
        if (contorAngajati < numarDeRanduri) {
            LocalDate currentDate = worklogEmployeeList.get(contorAngajati).getLocalDate();
            Employee currentEmployee = worklogEmployeeList.get(contorAngajati).getEmployee();
            boolean nationalHoliday = holidayService.verifyIfDateIsHoliday(currentDate);
            boolean verifyIfEmployeeInHoliday = holidayService.verifyIfDateIsHolidayForEmployee(currentEmployee.getId(),currentDate);
            boolean verifyIfEmployeeWasNotEmployedYet = currentEmployee.getDataAngajarii().isAfter(currentDate);
            DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
            if (dayOfWeek.equals(DayOfWeek.SATURDAY)
                    || dayOfWeek.equals(DayOfWeek.SUNDAY)
                       || nationalHoliday
                          || verifyIfEmployeeInHoliday
                             || verifyIfEmployeeWasNotEmployedYet) {
                cell.setCellValue("--");
            } else {
                cell.setCellValue("");
            }
        } else {
            cell.setCellValue("");
        }
    }
}
