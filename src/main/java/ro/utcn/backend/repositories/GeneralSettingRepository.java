package ro.utcn.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcn.backend.model.GeneralSetting;

@Repository
public interface GeneralSettingRepository extends JpaRepository<GeneralSetting, Long> {

    GeneralSetting findByNumeProprietate(String numeProprietate);
}
