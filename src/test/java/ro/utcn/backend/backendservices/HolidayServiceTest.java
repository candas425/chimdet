package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Employee;
import ro.utcn.backend.model.Holiday;
import ro.utcn.backend.model.enums.TipConcediu;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Testing HolidayService
 * <p>
 * Created by Lucas on 7/5/2017.
 */
public class HolidayServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        Employee employee = createAngajat();
        employeeService.saveEmployee(employee);

        Holiday holiday = createHoliday(employee, TipConcediu.Libera, LocalDate.now());
        holidayService.saveHoliday(holiday);
    }

    @Test
    public void saveHoliday() throws Exception {
        assertEquals(1, holidayService.getAllForEmployee(employeeService.getAll().get(0)).size());

        Holiday holiday = createHoliday(employeeService.getAll().get(0), TipConcediu.Libera, LocalDate.now().plusDays(1));
        holidayService.saveHoliday(holiday);

        assertEquals(2, holidayService.getAllForEmployee(employeeService.getAll().get(0)).size());
    }

    @Test
    public void saveNonEmployeeHoliday() throws Exception {
        assertEquals(1, holidayService.getAllForEmployee(employeeService.getAll().get(0)).size());

        Holiday holiday = createHoliday(null, TipConcediu.Libera, LocalDate.now().plusDays(1));
        holidayService.saveHoliday(holiday);

        assertEquals(1, holidayService.getAllNonEmployeeHolidays().size());
    }

    @Test
    public void verifyIfDateIsHoliday() throws Exception {
        Holiday holiday = createHoliday(null, TipConcediu.Libera, LocalDate.now());
        holidayService.saveHoliday(holiday);

        assertEquals(1, holidayService.getAllNonEmployeeHolidays().size());
        assertTrue(holidayService.verifyIfDateIsHoliday(LocalDate.now()));
    }

    @Test(expected = PersistanceException.class)
    public void saveHolidayExist() throws Exception {
        assertEquals(1, holidayService.getAllForEmployee(employeeService.getAll().get(0)).size());

        Holiday holiday = createHoliday(employeeService.getAll().get(0), TipConcediu.Libera, LocalDate.now());
        holidayService.saveHoliday(holiday);
    }

    @Test
    public void getAllNonEmployeeHolidays() throws Exception {
        assertEquals(0, holidayService.getAllNonEmployeeHolidays().size());
    }

    @Test
    public void getAllForEmployee() throws Exception {
        assertEquals(1, holidayService.getAllForEmployee(employeeService.getAll().get(0)).size());
    }

    @Test
    public void deleteHoliday() throws Exception {
        assertEquals(1, holidayService.getAllForEmployee(employeeService.getAll().get(0)).size());

        holidayService.deleteHoliday(holidayService.getAllForEmployee(employeeService.getAll().get(0)).get(0));

        assertEquals(0, holidayService.getAllForEmployee(employeeService.getAll().get(0)).size());
    }
}
