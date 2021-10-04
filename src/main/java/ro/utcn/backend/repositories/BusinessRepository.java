package ro.utcn.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcn.backend.model.Business;

import java.util.List;

@Repository
public interface BusinessRepository extends JpaRepository<Business, Long> {

    List<Business> findAllByAvailableInCommands(String availableInCommands);

    List<Business> findAllByNumeOrCui(String nume, String cui);
}
