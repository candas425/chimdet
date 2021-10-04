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
import ro.utcn.backend.backendservices.PriceService;
import ro.utcn.backend.backendservices.ProductService;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Price;
import ro.utcn.backend.model.Product;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.frontend.frontendServices.TableService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ro.utcn.exceptions.GeneralExceptions.IMPORT_PRET_FAILED;

/**
 * Pentru Preturi
 * Created by Lucian on 6/1/2017.
 */

@Component
public class ExcelPreturiParser {

    @Autowired
    private PriceService priceService;
    @Autowired
    private TableService tableService;
    @Autowired
    private ProductService productService;

    public void parseFileAndSave(File file, TextField textField, TableView tableView, Business business) throws GeneralExceptions, PersistanceException, IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            try (Workbook workbook = new XSSFWorkbook(inputStream)) {
                Sheet firstSheet = workbook.getSheetAt(0);
                Iterator<Row> iterator = firstSheet.iterator();

                List<Price> priceList = readExcelFile(iterator, business);

                StringBuilder message = new StringBuilder();

                for (Price price : priceList) {
                    try {
                        priceService.savePrice(price, business.getId());
                    } catch (Exception e) {
                        message.append(price.getProduct().getNume()).append(" ").append(e.getMessage()).append("\n");
                    }
                }

                tableService.refreshItemsFromPreturiTable(textField, tableView, business);
                if (!StringHelper.isEmpty(message.toString())) {
                    throw new GeneralExceptions(message.toString());
                }
            }
        }
    }

    private List<Price> readExcelFile(Iterator<Row> iterator, Business business) throws GeneralExceptions {
        List<Price> priceList = new ArrayList<>();

        List<Product> productList = productService.getAll();

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            Price price = new Price();
            int contor = 0;

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                if (contor == 2) {
                    throw new GeneralExceptions(IMPORT_PRET_FAILED);
                }
                switch (cell.getCellType()) {
                    case Cell.CELL_TYPE_STRING:
                        Product product = null;
                        for (Product product1 : productList) {
                            if (product1.getNume().equals(cell.getStringCellValue())) {
                                product = product1;
                                break;
                            }
                        }
                        if (product != null) {
                            price.setProduct(product);
                            productList.remove(product);
                        } else {
                            throw new GeneralExceptions("Numele produsului: " + cell.getStringCellValue() + " nu exista in baza de date");
                        }
                        break;
                    case Cell.CELL_TYPE_NUMERIC:
                        price.setPretUnitar(cell.getNumericCellValue());
                        break;
                }
                contor++;

            }
            if (price.getProduct() == null || price.getPretUnitar() == 0.0) {
                throw new GeneralExceptions(IMPORT_PRET_FAILED);
            }
            price.setBusiness(business);
            priceList.add(price);
        }

        for (Product product : productList) {
            Price price = new Price();
            price.setProduct(product);
            price.setBusiness(business);
            price.setPretUnitar(0.0);
            priceList.add(price);
        }

        return priceList;
    }
}
