package ro.utcn.backend.backendservices;

import org.springframework.stereotype.Component;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.factory.IEntity;

/**
 * Abstract factory class
 *
 * Created by Lucas on 4/15/2017.
 */

@Component
public abstract class BaseService {

    public abstract void update(IEntity entity) throws PersistanceException;
}
