package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.enums.TipFirma;

import static org.junit.Assert.assertEquals;

/**
 * Testarea FirmeService
 * <p>
 * Created by Lucas on 7/5/2017.
 */
public class BusinessServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        Business business = createBusiness();
        businessService.saveBusiness(business);
    }

    @Test
    public void saveBusiness() throws Exception {
        assertEquals(1, businessService.getAll().size());

        Business business = createBusiness("FIRMA", "BANCA", "CUI", "HUNEDARA", "REGCOM", "SEDIUL", TipFirma.NORMAL);
        businessService.saveBusiness(business);

        assertEquals(2, businessService.getAll().size());
    }

    @Test(expected = PersistanceException.class)
    public void saveBusinessExists() throws Exception {
        assertEquals(1, businessService.getAll().size());

        Business business = createBusiness();
        businessService.saveBusiness(business);
    }

    @Test
    public void getAll() throws Exception {
        assertEquals(1, businessService.getAll().size());
    }

    @Test
    public void getBusinessAvailableInCommands() throws Exception {
        assertEquals(1, businessService.getBusinessAvailableInCommands().size());
    }

    @Test
    public void update() throws Exception {
        assertEquals(1, businessService.getAll().size());

        Business business = businessService.getAll().get(0);
        business.setCui("DAA");

        businessService.update(business);
        assertEquals("DAA", businessService.getAll().get(0).getCui());
    }

    @Test
    public void delete() throws Exception {
        assertEquals(1, businessService.getAll().size());
        Business business = businessService.getAll().get(0);
        business.setCui("DAA");

        businessService.deleteBusiness(business);

        assertEquals(0, businessService.getAll().size());
    }
}
