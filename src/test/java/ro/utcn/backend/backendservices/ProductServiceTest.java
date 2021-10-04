package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Product;
import ro.utcn.backend.model.enums.TipProdus;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * ProdusService testing
 * Created by Lucas on 7/5/2017.
 */
public class ProductServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        Product product = createProduct();
        productService.saveProduct(product);
    }

    @Test(expected = PersistanceException.class)
    public void saveProductExist() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = createProduct();
        productService.saveProduct(product);
    }

    @Test
    public void saveProduct() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = createProduct("Nume", 5.0, "0006", TipProdus.DETERGENT);
        productService.saveProduct(product);

        assertEquals(2, productService.getAll().size());
    }

    @Test
    public void getAvailableProductsInCommandTable() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = createProduct("Nume", 5.0, "0006", TipProdus.DETERGENT);
        product.setExistaInTabelComenzi("Adevarat");
        productService.saveProduct(product);

        assertEquals(1, productService.getAvailableProductsInCommandTable().size());
    }

    @Test
    public void getProductByIdentifierCode() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = createProduct("Nume", 5.0, "0006", TipProdus.DETERGENT);
        productService.saveProduct(product);

        assertNotNull(productService.getProductByIdentifierCode("0006"));
    }

    @Test
    public void getProductCantityById() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = createProduct("Nume", 5.0, "0006", TipProdus.DETERGENT);
        productService.saveProduct(product);

        assertEquals(5.0, productService.getProductCantityById(product.getId()), 0.001);
    }

    @Test
    public void getProductsByIds() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = productService.getAll().get(0);

        Set<Integer> integerSet = new HashSet<>();
        integerSet.add(product.getId());

        assertEquals(1, productService.getProductsByIds(integerSet).size());
    }

    @Test
    public void getProductByIdCodeAndAvailableInTable() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = createProduct("Nume", 5.0, "0006", TipProdus.DETERGENT);
        product.setExistaInTabelComenzi("Adevarat");
        productService.saveProduct(product);

        assertNotNull(productService.getProductByIdCodeAndAvailableInTable("0006"));
    }

    @Test
    public void deleteProduct() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = productService.getAll().get(0);
        productService.deleteProduct(product);

        assertEquals(0, productService.getAll().size());
    }

    @Test
    public void update() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = productService.getAll().get(0);
        product.setCantitate(20.0);
        productService.update(product);

        assertEquals(20.0, productService.getAll().get(0).getCantitate(), 0.001);
    }

    @Test(expected = PersistanceException.class)
    public void updateSameCodeExists() throws Exception {
        assertEquals(1, productService.getAll().size());

        Product product = createProduct("Nume", 20.0, "0006", TipProdus.DETERGENT);
        product.setExistaInTabelComenzi("Adevarat");
        productService.saveProduct(product);

        product.setIdentificatorProdus("0007");
        productService.update(product);
    }

    @Test
    public void getAll() throws Exception {
        assertEquals(1, productService.getAll().size());
    }
}
