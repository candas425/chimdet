package ro.utcn.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ro.utcn.backend.model.Holiday;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findAllByEmployeeIdIsNull();

    Holiday findByEmployeeIdIsNullAndData(LocalDate date);

    Holiday findByEmployeeIdAndData(int employeeId, LocalDate date);

    List<Holiday> findAllByEmployeeId(int employeeId);
}
