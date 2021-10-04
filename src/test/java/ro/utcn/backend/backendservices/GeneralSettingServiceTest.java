package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.GeneralSetting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * SetareGeneralaService Testing
 *
 * Created by Lucas on 7/5/2017.
 */
public class GeneralSettingServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        GeneralSetting generalSetting = new GeneralSetting();
        generalSetting.setNumeProprietate("NUME");
        generalSetting.setValoareProprietate("200");

        generalSettingService.saveGeneralSetting(generalSetting);
    }


    @Test
    public void saveGeneralSetting() throws Exception {
        assertNotNull(generalSettingService.getGeneralSetting("NUME"));

        GeneralSetting generalSetting = new GeneralSetting();
        generalSetting.setNumeProprietate("NUME1");
        generalSetting.setValoareProprietate("200");
        generalSettingService.saveGeneralSetting(generalSetting);

        assertNotNull(generalSettingService.getGeneralSetting("NUME1"));
    }

    @Test
    public void saveGeneralSettingAlreadyExists() throws Exception {
      GeneralSetting generalSetting = generalSettingService.getGeneralSetting("NUME");

      assertEquals( "200", generalSetting.getValoareProprietate());
      generalSetting.setValoareProprietate("202");
      generalSettingService.saveGeneralSetting(generalSetting);

      assertEquals( "202", generalSetting.getValoareProprietate());
    }

    @Test
    public void getGeneralSetting() throws Exception {
        assertNotNull(generalSettingService.getGeneralSetting("NUME"));
    }

}
