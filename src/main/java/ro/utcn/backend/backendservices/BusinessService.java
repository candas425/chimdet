package ro.utcn.backend.backendservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.factory.IEntity;
import ro.utcn.backend.repositories.BusinessRepository;

import java.util.List;

/**
 * Made by Lucian on 3/26/2017.
 */

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
public class BusinessService extends BaseService {

    @Autowired
    private BusinessRepository businessRepository;

    public List<Business> getBusinessAvailableInCommands() {
        return businessRepository.findAllByAvailableInCommands("Adevarat");
    }

    public void saveBusiness(Business business) throws PersistanceException {
        List<Business> businessList = businessRepository.findAllByNumeOrCui(business.getNume(), business.getCui());
        if (businessList.isEmpty()) {
            businessRepository.save(business);
        } else {
            throw new PersistanceException(PersistanceException.OBJECT_EXIST);
        }
    }

    public void deleteBusiness(Business business) throws PersistanceException {
        businessRepository.delete(business);
    }

    public List<Business> getAll() {
        return businessRepository.findAll();
    }

    @Override
    public void update(IEntity IEntity) throws PersistanceException {
        businessRepository.save((Business) IEntity);
    }
}
