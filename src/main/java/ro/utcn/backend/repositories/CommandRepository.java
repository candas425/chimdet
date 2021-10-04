package ro.utcn.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcn.backend.model.Command;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommandRepository extends JpaRepository<Command, Long> {

    List<Command> findAllByBusinessIdAndAvailableInTableComandaTrue(int businessId);

    List<Command> findAllByDataGreaterThanEqualAndDataLessThanEqual(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo);

    List<Command> findAllByDataGreaterThanEqualAndDataLessThanEqualAndBusinessNume(LocalDateTime localDateTimeFrom, LocalDateTime localDateTimeTo, String businessNume);

    List<Command> findAllByBusinessIdAndOras(int businessId, String commandOras);

    List<Command> findAllByBusinessIdOrderByDataDesc(int businessId);
}
