package ro.utcn.backend.backendservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Business;
import ro.utcn.backend.model.Command;
import ro.utcn.backend.repositories.CommandRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Lucian on 4/7/2017.
 */

@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
public class CommandService {

    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private ProductService productService;

    public List<Command> getCommandsAvailableInTableComanda(Business business) {
        return commandRepository.findAllByBusinessIdAndAvailableInTableComandaTrue(business.getId());
    }

    public Map<Double, Integer> countProductFromCommandsFromDateToDate(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo) {
        List<Command> commandList = commandRepository.findAllByDataGreaterThanEqualAndDataLessThanEqual(localDateTimeFrom, localDateTimeTo);
        return parseProductsSold(commandList);
    }

    public Map<Double, Integer> countProductFromCommandsFromDateToDateWithBusinessName(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo, String businessName) {
        List<Command> commandList = commandRepository.findAllByDataGreaterThanEqualAndDataLessThanEqualAndBusinessNume(localDateTimeFrom, localDateTimeTo, businessName);
        return parseProductsSold(commandList);
    }

    private Map<Double, Integer> parseProductsSold(List<Command> commandList) {
        Map<Double, Integer> productsMap = new HashMap<>();

        for (Command command : commandList) {
            for (Map.Entry<Integer, Integer> entry : command.getListaProduse().entrySet()) {
                double cantitate = productService.getProductCantityById(entry.getKey());
                if (productsMap.get(cantitate) != null) {
                    productsMap.put(cantitate, productsMap.get(cantitate) + entry.getValue());
                } else {
                    productsMap.put(cantitate, entry.getValue());
                }
            }

        }
        return productsMap;
    }

    public List<Command> getCommandsForBusinessAndCity(Business business, String numeOras) {
        return commandRepository.findAllByBusinessIdAndOras(business.getId(), numeOras);
    }

    public List<Command> getCommandsForBusiness(Business business) {
        return commandRepository.findAllByBusinessIdOrderByDataDesc(business.getId());
    }

    public void saveCommand(Command command) throws PersistanceException {
        commandRepository.save(command);
    }

    public void deleteCommand(Command command) throws PersistanceException {
        commandRepository.delete(command);
    }

    public void updateCommand(Command command) throws PersistanceException {
        commandRepository.save(command);
    }
}
