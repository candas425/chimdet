package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Price;
import ro.utcn.backend.model.Product;

import static org.junit.Assert.assertEquals;

/**
 * PretService testing
 * <p>
 * Created by Lucas on 7/5/2017.
 */
public class PriceServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        Business business = createBusiness();
        businessService.saveBusiness(business);

        Product product = createProduct();
        productService.saveProduct(product);

        Price price = createPrice(business, product);
        priceService.savePrice(price, business.getId());
    }

    @Test
    public void saveWithUpdate() throws Exception {
        Price price = priceService.getAll(businessService.getAll().get(0)).get(0);
        price.setPretUnitar(25);

        priceService.savePrice(price, businessService.getAll().get(0).getId());
        assertEquals(25, price.getPretUnitar(), 0.001);
    }

    @Test
    public void delete() throws Exception {
        assertEquals(1, priceService.getAll(businessService.getAll().get(0)).size());

        Price price = priceService.getAll(businessService.getAll().get(0)).get(0);
        priceService.deletePrice(price);

        assertEquals(0, priceService.getAll(businessService.getAll().get(0)).size());
    }

    @Test
    public void update() throws Exception {

        Price price = priceService.getAll(businessService.getAll().get(0)).get(0);
        price.setPretUnitar(10.0);

        priceService.update(price);
        assertEquals(10,0, priceService.getAll(businessService.getAll().get(0)).get(0).getPretUnitar());
    }

    @Test
    public void getAll() throws Exception {
        assertEquals(1, priceService.getAll(businessService.getAll().get(0)).size());
    }

}
