package ro.utcn.frontend.importer;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import ro.utcn.Constants;
import ro.utcn.exceptions.GeneralExceptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static ro.utcn.Constants.ANUL_KEYWORD;
import static ro.utcn.Constants.LUNA_KEYWORD;
import static ro.utcn.Constants.NUME_KEYWORD;
import static ro.utcn.exceptions.GeneralExceptions.FISIERUL_DE_PONTAJ_NU_ARE_DATE_SUFICIENTE;
import static ro.utcn.exceptions.GeneralExceptions.FISIERUL_EXCEL_CONTINE_PREA_MULTI_ANGAJATI;
import static ro.utcn.exceptions.GeneralExceptions.LIPSA_FISIER_PONTAJ;


/**
 * Pontaj
 * Created by Lucian on 6/3/2017.
 */

@Component
public class ExcelPontajParser {

    private static final String ZIUA_KEYWORD = "ziua=";
    private static final String TOTAL_KEYWORD = "{total}";
    private static final String ORE_CO_KEYWORD = "{oreCo}";


    public void parseFile(List<List<String>> listaDeAfisat, String luna, String anul) throws IOException, GeneralExceptions {

        try (FileInputStream inputStream = new FileInputStream(Constants.FILES_LOCATION + Constants.PONTAJ_FILE)) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet firstSheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = firstSheet.iterator();

                int stringListSize = listaDeAfisat.get(0).size();
                int numersOfDays = stringListSize - 3;
                int numarDeAngajati = listaDeAfisat.size();

                int contorAngajati = 0;
                while (iterator.hasNext()) {
                    Row nextRow = iterator.next();
                    Iterator<Cell> cellIterator = nextRow.cellIterator();

                    int contorZile = 1;
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                            if (cell.getStringCellValue().contains(LUNA_KEYWORD)) {
                                String content = cell.getStringCellValue();
                                content = content.replace(LUNA_KEYWORD, luna);
                                content = content.replace(ANUL_KEYWORD, anul);
                                cell.setCellValue(content);
                            } else if (cell.getStringCellValue().equals(NUME_KEYWORD)) {
                                cell.setCellValue(listaDeAfisat.get(contorAngajati).get(0));
                            } else if (cell.getStringCellValue().contains(ZIUA_KEYWORD + contorZile)) {
                                cell.setCellValue(listaDeAfisat.get(contorAngajati).get(contorZile));
                                if (contorZile == numersOfDays) {
                                    contorZile = 0;
                                } else {
                                    contorZile++;
                                }
                            } else if (cell.getStringCellValue().contains(ZIUA_KEYWORD)) {
                                cell.setCellValue("");
                            } else if (cell.getStringCellValue().contains(TOTAL_KEYWORD)) {
                                cell.setCellValue(listaDeAfisat.get(contorAngajati).get(stringListSize - 2));
                            } else if (cell.getStringCellValue().contains(ORE_CO_KEYWORD)) {
                                cell.setCellValue(listaDeAfisat.get(contorAngajati).get(stringListSize - 1));
                                if (contorAngajati + 1 < numarDeAngajati) {
                                    contorAngajati++;
                                }
                            }
                        }
                    }

                }

                if (numarDeAngajati != (contorAngajati + 1)) {
                    throw new GeneralExceptions(FISIERUL_DE_PONTAJ_NU_ARE_DATE_SUFICIENTE);
                }

                FileOutputStream outputStream = new FileOutputStream(Constants.FILES_LOCATION + Constants.PONTAJ_RESULT_FILE);
                workbook.write(outputStream);
                outputStream.close();
            }

        } catch (IndexOutOfBoundsException e) {
            throw new GeneralExceptions(FISIERUL_EXCEL_CONTINE_PREA_MULTI_ANGAJATI + " - " + e.getMessage());
        } catch (FileNotFoundException e) {
            throw new GeneralExceptions(LIPSA_FISIER_PONTAJ + " - " + e.getMessage());
        } catch (Exception e) {
            if (e.getMessage().equals(FISIERUL_DE_PONTAJ_NU_ARE_DATE_SUFICIENTE)) {
                throw new GeneralExceptions(FISIERUL_DE_PONTAJ_NU_ARE_DATE_SUFICIENTE);
            } else {
                throw new GeneralExceptions(e.getMessage());
            }
        }
    }
}
