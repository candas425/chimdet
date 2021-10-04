package ro.utcn.frontend.frontendServices;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ro.utcn.backend.backendservices.ProductService;
import ro.utcn.backend.model.Product;
import ro.utcn.frontend.frontendServices.helper.KeyPressedHelper;
import ro.utcn.frontend.frontendServices.helper.ListRowComanda;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucian on 5/4/2017.
 */

@Service
public class ListViewService {

    private Logger LOGGER = LogManager.getLogger(ListViewService.class);

    @Autowired
    private ProductService productService;
    @Autowired
    private PopUpService popUpService;

    public void constructComandaListaList(ListView<Product> listView, ListView<ListRowComanda> secondListView, Label textCautat) {
        List<Product> productList = productService.getAll();

        KeyPressedHelper keyPressedHelper = new KeyPressedHelper();
        keyPressedHelper.setWord("");

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                popUpService.showPopUpWithOneChoiceForProdusCantity(listView, secondListView);
            }
        });
        listView.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                if (keyPressedHelper.getWord().length() > 0) {
                    keyPressedHelper.setWord(keyPressedHelper.getWord().substring(0, keyPressedHelper.getWord().length() - 1));
                }
            } else {
                if(event.getCode().equals(KeyCode.SPACE)) {
                    keyPressedHelper.setWord(keyPressedHelper.getWord() + " ");
                }else if(event.getCode().equals(KeyCode.PERIOD)){
                    keyPressedHelper.setWord(keyPressedHelper.getWord() + ".");
                }else if(event.getCode().equals(KeyCode.COMMA)){
                    keyPressedHelper.setWord(keyPressedHelper.getWord() + ",");
                }else if(event.getCode().isLetterKey()){
                    keyPressedHelper.setWord(keyPressedHelper.getWord() + event.getCode().toString().toLowerCase());
                }else if(event.getCode().equals(KeyCode.ENTER)){
                    popUpService.showPopUpWithOneChoiceForProdusCantity(listView, secondListView);
                }
            }
            textCautat.setText(keyPressedHelper.getWord());
            changeProdusList(keyPressedHelper.getWord(), productList, listView);
        });
        listView.setItems(FXCollections.observableArrayList(productList));
    }

    private void changeProdusList(String name, List<Product> productList, ListView<Product> produsListView) {
        List<Product> newProductList = new ArrayList<>();

        for (Product product : productList) {
            if (product.getNume().toLowerCase().contains(name)) {
                newProductList.add(product);
            }
        }
        produsListView.setItems(FXCollections.observableArrayList(newProductList));
    }

}
