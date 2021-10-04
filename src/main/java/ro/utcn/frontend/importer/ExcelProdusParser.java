package ro.utcn.frontend.importer;

import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.internal.util.StringHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.backendservices.ProductService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Product;
import ro.utcn.backend.model.enums.TipProdus;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.TableService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ro.utcn.exceptions.GeneralExceptions.IMPORT_FAILED;


/**
 * Excel parser
 * Created by Lucian on 5/30/2017.
 */

@Component
public class ExcelProdusParser {

    @Autowired
    private ProductService productService;
    @Autowired
    private TableService tableService;

    public void parseFileAndSave(File file, TextField textField, TableView tableView) throws GeneralExceptions, PersistanceException, IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet firstSheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = firstSheet.iterator();

                List<Product> productList = readExcelFile(iterator);

                StringBuilder message = new StringBuilder();

                for (Product product : productList) {
                    try {
                        productService.saveProduct(product);
                    } catch (Exception e) {
                        message.append(product.getNume()).append(" ").append(e.getMessage()).append("\n");
                    }
                }

                tableService.refreshItemsFromProdusTableWithFilter(textField, tableView);
                if (!StringHelper.isEmpty(message.toString())) {
                    throw new GeneralExceptions(message.toString());
                }
            }
        }
    }

    private List<Product> readExcelFile(Iterator<Row> iterator) throws GeneralExceptions {
        List<Product> productList = new ArrayList<>();

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            Product product = new Product();
            int contor = 0;

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                if (contor == 4) {
                    throw new GeneralExceptions(IMPORT_FAILED);
                }
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        if (contor == 0) {
                            product.setNume(cell.getStringCellValue());
                        } else if (contor == 2) {
                            String value = cell.getStringCellValue();
                            if (value.equals("DETERGENT")) {
                                product.setTip(TipProdus.DETERGENT);
                            } else if (value.equals("SAPUN")) {
                                product.setTip(TipProdus.SAPUN);
                            }
                        } else if (contor == 3) {
                            product.setIdentificatorProdus(cell.getStringCellValue());
                        }
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        product.setCantitate(cell.getNumericCellValue());
                        break;
                }
                contor++;

            }
            if (product.getNume() == null || product.getTip() == null || product.getCantitate() == 0.0) {
                throw new GeneralExceptions(IMPORT_FAILED);
            }
            productList.add(product);
        }
        return productList;
    }

}

