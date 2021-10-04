package ro.utcn.frontend.exporter;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.backend.backendservices.PriceService;
import ro.utcn.backend.backendservices.ProductService;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Price;
import ro.utcn.backend.model.Product;

import java.util.List;

/**
 * Created by Lucian on 6/1/2017.
 */

@Component
public class ExcelExporter {

    @Autowired
    private ProductService productService;
    @Autowired
    private PriceService priceService;

    public Workbook createExcelFileProduse() {
        List<Product> productList = productService.getAll();

        Workbook workbook = new XSSFWorkbook();
        Sheet produseSheet = workbook.createSheet("Produse");

        int rowIndex = 0;

        for (Product product : productList) {
            Row row = produseSheet.createRow(rowIndex++);
            int cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(product.getNume());
        }

        return workbook;
    }

    public Workbook createExcelFilePreturi(Business business) {
        List<Price> priceList = priceService.getAll(business);

        Workbook workbook = new XSSFWorkbook();
        Sheet produseSheet = workbook.createSheet("Oferta pret");

        int rowIndex = 0;

        Row row = produseSheet.createRow(rowIndex++);
        int cellIndex = 0;
        row.createCell(cellIndex++).setCellValue("PRET UNITAR");
        row.createCell(cellIndex++).setCellValue("CANTITATE");
        row.createCell(cellIndex++).setCellValue("PRODUS");

        for (Price price : priceList) {
            row = produseSheet.createRow(rowIndex++);
            cellIndex = 0;
            row.createCell(cellIndex++).setCellValue(price.getPretUnitar());
            row.createCell(cellIndex++).setCellValue(price.getProduct().getCantitate());
            row.createCell(cellIndex++).setCellValue(price.getProduct().getNume());
        }

        return workbook;
    }
}
