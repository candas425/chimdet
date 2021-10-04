package ro.utcn.backend.backendservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Employee;
import ro.utcn.backend.model.Holiday;
import ro.utcn.backend.repositories.HolidayRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * Holiday
 * Created by Lucian on 6/5/2017.
 */

@Service
@Transactional
public class HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;

    public void saveHoliday(Holiday holiday) throws PersistanceException {
        Holiday holidayFromDb;
        if (holiday.getEmployee() != null) {
            holidayFromDb = holidayRepository.findByEmployeeIdAndData(holiday.getEmployee().getId(), holiday.getData());
        } else {
            holidayFromDb = holidayRepository.findByEmployeeIdIsNullAndData(holiday.getData());
        }

        if (holidayFromDb == null) {
            holidayRepository.save(holiday);
        } else {
            throw new PersistanceException(PersistanceException.OBJECT_EXIST);
        }
    }

    public void deleteHoliday(Holiday holiday) throws PersistanceException {
        holidayRepository.delete(holiday);
    }

    public List<Holiday> getAllNonEmployeeHolidays() {
        return holidayRepository.findAllByEmployeeIdIsNull();
    }

    public List<Holiday> getAllForEmployee(Employee employee) {
        return holidayRepository.findAllByEmployeeId(employee.getId());
    }

    /**
     * Used for verifying if a date is national holiday or not
     */
    public boolean verifyIfDateIsHoliday(LocalDate localDate) {
        Holiday holiday = holidayRepository.findByEmployeeIdIsNullAndData(localDate);
        return holiday != null;
    }

    /**
     * Used for verifying if date is holiday for employee or not
     */
    public boolean verifyIfDateIsHolidayForEmployee(int employeeId, LocalDate localDate) {
        Holiday holiday = holidayRepository.findByEmployeeIdAndData(employeeId, localDate);
        return holiday != null;
    }
}
