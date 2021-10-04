package ro.utcn.backend.backendservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.GeneralSetting;
import ro.utcn.backend.repositories.GeneralSettingRepository;

/**
 * Used for general setting
 * <p>
 * Created by Lucian on 3/31/2017.
 */


@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
public class GeneralSettingService {

    @Autowired
    private GeneralSettingRepository generalSettingRepository;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    public void saveGeneralSetting(GeneralSetting generalSetting) throws PersistanceException {
        GeneralSetting generalSettingFromDb = generalSettingRepository.findByNumeProprietate(generalSetting.getNumeProprietate());
        if (generalSettingFromDb == null) {
            generalSettingRepository.save(generalSetting);
        } else {
            generalSettingFromDb.setValoareProprietate(generalSetting.getValoareProprietate());
            generalSettingRepository.save(generalSettingFromDb);
        }
    }

    public GeneralSetting getGeneralSetting(String numeProprietate) {
        return generalSettingRepository.findByNumeProprietate(numeProprietate);
    }

}
