package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Command;
import ro.utcn.backend.model.Product;
import ro.utcn.backend.model.enums.TipProdus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Testare comanda
 * <p>
 * Created by Lucas on 7/5/2017.
 */
public class CommandServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        Business business = createBusiness();
        businessService.saveBusiness(business);

        Product product = createProduct();
        Product product1 = createProduct("Sapun2", 5.0, "0001", TipProdus.SAPUN);
        Product product2 = createProduct("Sapun3", 5.0, "0002", TipProdus.SAPUN);

        productService.saveProduct(product);
        productService.saveProduct(product1);
        productService.saveProduct(product2);

        Map<Integer, Integer> integerIntegerMap = new HashMap<>();
        integerIntegerMap.put(product.getId(), 5);
        integerIntegerMap.put(product1.getId(), 6);
        integerIntegerMap.put(product2.getId(), 7);


        Command command = createComanda(LocalDateTime.now(), business, integerIntegerMap, "ORASTIE");
        commandService.saveCommand(command);
    }

    @Test
    public void getCommandsForBusiness() throws Exception {
        Business business = businessService.getAll().get(0);
        assertEquals(1, commandService.getCommandsForBusiness(business).size());
    }

    @Test
    public void deleteCommand() throws Exception {
        Business business = businessService.getAll().get(0);
        Command command = commandService.getCommandsForBusiness(business).get(0);

        commandService.deleteCommand(command);

        assertEquals(0, commandService.getCommandsForBusiness(business).size());
    }
    @Test
    public void updateCommand() throws Exception {
        Business business = businessService.getAll().get(0);
        Command command = commandService.getCommandsForBusiness(business).get(0);
        command.setOras("ACOLO");
        commandService.updateCommand(command);

        assertEquals("ACOLO", commandService.getCommandsForBusiness(business).get(0).getOras());
    }

    @Test
    public void countProductFromCommandsFromDateToDate() throws Exception {
        Map<Double, Integer> productsMap = commandService.countProductFromCommandsFromDateToDate(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
        assertEquals(18, productsMap.get(5.0D), 0.001);
    }

    @Test
    public void countProductFromCommandsFromDateToDateWithBusinessName() throws Exception {
        Business business = businessService.getAll().get(0);
        Map<Double, Integer> productsMap = commandService.countProductFromCommandsFromDateToDateWithBusinessName(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1), business.getNume());
        assertEquals(18, productsMap.get(5.0D), 0.001);
    }

    @Test
    public void getCommandsForBusinessAndCity() throws Exception {
        Business business = businessService.getAll().get(0);
        assertEquals(1, commandService.getCommandsForBusinessAndCity(business, "ORASTIE").size());
    }

    @Test
    public void getCommandsFromLastResetTime() throws Exception {
        Business business = businessService.getAll().get(0);
        assertEquals(1, commandService.getCommandsAvailableInTableComanda(business).size());
    }

}
