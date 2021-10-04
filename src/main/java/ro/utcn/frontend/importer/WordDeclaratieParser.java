package ro.utcn.frontend.importer;


import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ro.utcn.Constants;
import ro.utcn.backend.backendservices.ProductService;
import ro.utcn.backend.model.Command;
import ro.utcn.backend.model.Product;
import ro.utcn.exceptions.GeneralExceptions;
import ro.utcn.helper.JavaFxComponentsHelper;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ro.utcn.helper.GeneralHelper.WHITE_SPACE;

/**
 * Word parser
 * <p>
 * Created by Lucian on 6/3/2017.
 */

@Component
public class WordDeclaratieParser {

    @Autowired
    private ProductService productService;

    public void parseWordFileFirstPage(Command command) throws GeneralExceptions, IOException {
        try (FileInputStream inputStream = new FileInputStream(Constants.FILES_LOCATION + Constants.DECLARATIE1_DOCX)) {
            XWPFDocument firstPageDocument = new XWPFDocument(inputStream);

            int ok = 0;
            for (XWPFParagraph paragraph : firstPageDocument.getParagraphs()) {
                if (paragraph.getText().contains("Beneficiar")) {
                    paragraph.getRuns().get(0).setText(":" + command.getBusiness().getNume());
                    ok = 1;
                }
            }

            if (ok != 1) {
                throw new GeneralExceptions("Textul 'Beneficiar' lipseste din document");
            }

            FileOutputStream firstFileOutputStream = new FileOutputStream(Constants.FILES_LOCATION + Constants.DECLARATIE1_RESULT_FILE_DOCX);
            firstPageDocument.write(firstFileOutputStream);
            firstFileOutputStream.close();

            //print files
            JavaFxComponentsHelper.printFile(Constants.FILES_LOCATION + Constants.DECLARATIE1_RESULT_FILE_DOCX);
        } catch (IOException e) {
            throw new GeneralExceptions(GeneralExceptions.LIPSA_FISIER_DECLARATIE_1 + " - " + e.getMessage());
        }
    }

    public String parseWordFileSecondPage(List<Command> comandList) throws GeneralExceptions, IOException {
        try (FileInputStream secondInputStream = new FileInputStream(Constants.FILES_LOCATION + Constants.DECLARATIE2_DOCX)) {

            Map<Integer, Integer> concatenatedProdusList = new HashMap<>();
            for (Command command : comandList) {
                for (Map.Entry<Integer, Integer> intrare : command.getListaProduse().entrySet()) {
                    concatenatedProdusList.merge(intrare.getKey(), intrare.getValue(), (a, b) -> a + b);
                }
            }

            List<Product> productList = productService.getProductsByIds(concatenatedProdusList.keySet());
            XWPFDocument secondPageDocument = new XWPFDocument(secondInputStream);
            //create table
            List<XWPFTable> table = secondPageDocument.getTables();
            XWPFTable tableToParse = table.get(0);

            for (int i = 2; i < tableToParse.getRows().size(); i++) {
                XWPFTableCell nameColumn = tableToParse.getRows().get(i).getTableCells().get(1);
                XWPFTableCell x05Column = tableToParse.getRows().get(i).getTableCells().get(4);
                XWPFTableCell x1Column = tableToParse.getRows().get(i).getTableCells().get(5);
                XWPFTableCell x5Column = tableToParse.getRows().get(i).getTableCells().get(6);
                XWPFTableCell x20Column = tableToParse.getRows().get(i).getTableCells().get(7);

                String[] productName = nameColumn.getText().split(WHITE_SPACE);
                String lastName = productName[productName.length - 1].replace(".", "");

                List<Product> productListContained = doesProdusListContainsNameFromWordFile(lastName, productList);
                if (!productListContained.isEmpty()) {
                    double cantityForX05 = getXCantity(productListContained, concatenatedProdusList, 0.5);
                    double cantityForX1 = getXCantity(productListContained, concatenatedProdusList, 1.0);
                    double cantityForX5 = getXCantity(productListContained, concatenatedProdusList, 5.0);
                    double cantityForX20 = getXCantity(productListContained, concatenatedProdusList, 20.0);

                    if (cantityForX05 != 0.0) {
                        x05Column.setText(String.valueOf((int) cantityForX05));
                    }
                    if (cantityForX1 != 0.0) {
                        x1Column.setText(String.valueOf((int) cantityForX1));
                    }
                    if (cantityForX5 != 0.0) {
                        x5Column.setText(String.valueOf((int) cantityForX5));
                    }
                    if (cantityForX20 != 0.0) {
                        x20Column.setText(String.valueOf((int) cantityForX20));
                    }
                    productList.removeAll(productListContained);
                }
            }

            FileOutputStream secondFileOutputStream = new FileOutputStream(Constants.FILES_LOCATION + Constants.DECLARATIE2_RESULT_FILE_DOCX);
            secondPageDocument.write(secondFileOutputStream);
            secondFileOutputStream.close();

            JavaFxComponentsHelper.printFile(Constants.FILES_LOCATION + Constants.DECLARATIE2_RESULT_FILE_DOCX);
            if (!productList.isEmpty()) {
                return "Produsele/Produsul:\n" + productList + "\n din comanda nu a reusit sa se potriveasca cu nici un \n nume din fisierul word, te rog schimba numele in fisierul word sau la produs";
            }

            return "";
        } catch (IOException e) {
            throw new GeneralExceptions(GeneralExceptions.LIPSA_FISIER_DECLARATIE_2 + " - " + e.getMessage());
        }
    }


    private List<Product> doesProdusListContainsNameFromWordFile(String name, List<Product> productList) {
        List<Product> productListResult = new ArrayList<>();
        for (Product product : productList) {
            if (product.getNume().toLowerCase().contains(name.toLowerCase())) {
                productListResult.add(product);
            }
        }
        return productListResult;
    }

    private double getXCantity(List<Product> productList, Map<Integer, Integer> integerIntegerMap, double cantityForProdus) {
        double cantity = 0.0;
        for (Product product : productList) {
            if (product.getCantitate() == cantityForProdus) {
                cantity += integerIntegerMap.get(product.getId());
            }
        }
        return cantity;
    }

}
