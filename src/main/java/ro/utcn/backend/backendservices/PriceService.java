package ro.utcn.backend.backendservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Price;
import ro.utcn.backend.model.factory.IEntity;
import ro.utcn.backend.repositories.PriceRepository;

import java.util.List;

/**
 * Price
 * Created by Lucian on 5/31/2017.
 */

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
public class PriceService extends BaseService {

    @Autowired
    private PriceRepository priceRepository;

    public void savePrice(Price price, int businessId) throws PersistanceException {
        Price priceFromDb = priceRepository.findByBusinessIdAndProductId(businessId, price.getProduct().getId());
        if (priceFromDb != null) {
            priceFromDb.setPretUnitar(price.getPretUnitar());
            priceRepository.save(priceFromDb);
        } else {
            priceRepository.save(price);
        }
    }

    public void deletePrice(Price price) throws PersistanceException {
        priceRepository.delete(price);
    }

    public List<Price> getAll(Business business) {
        return priceRepository.findAllByBusinessIdOrderByProductCantitate(business.getId());
    }

    @Override
    public void update(IEntity IEntity) throws PersistanceException {
        priceRepository.save((Price) IEntity);
    }
}
