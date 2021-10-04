package ro.utcn.backend.backendservices;

import org.junit.Before;
import org.junit.Test;
import ro.utcn.BaseTest;
import ro.utcn.backend.model.Holiday;
import ro.utcn.backend.model.enums.TipConcediu;
import ro.utcn.backend.repositories.exceptions.PersistanceException;
import ro.utcn.backend.model.Employee;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

/**
 * Testare angajat service
 * <p>
 * Created by Lucas on 7/5/2017.
 */

public class EmployeeServiceTest extends BaseTest {

    @Before
    public void setUp() throws PersistanceException {
        Employee employee = createAngajat();
        employeeService.saveEmployee(employee);
    }

    @Test
    public void saveEmployee() throws Exception {
        assertEquals(1, employeeService.getAll().size());

        Employee employee = createAngajat("2412412124", "Lucian", 15, LocalDate.now());
        employeeService.saveEmployee(employee);

        assertEquals(2, employeeService.getAll().size());
    }

    @Test(expected = PersistanceException.class)
    public void saveEmployeeExist() throws Exception {
        assertEquals(1, employeeService.getAll().size());

        Employee employee = createAngajat();
        employeeService.saveEmployee(employee);
    }

    @Test
    public void getAll() throws Exception {
        assertEquals(1, employeeService.getAll().size());
    }

    @Test
    public void update() throws Exception {
        assertEquals(1, employeeService.getAll().size());

        Employee employee = employeeService.getAll().get(0);
        employee.setNume("Marian");

        employeeService.update(employee);

        Employee employeeFromDb = employeeService.getAll().get(0);
        assertEquals("Marian", employeeFromDb.getNume());
    }

    @Test
    public void deleteEmployee() throws Exception {
        assertEquals(1, employeeService.getAll().size());
        Employee employee = employeeService.getAll().get(0);

        employeeService.deleteEmployee(employee);

        assertEquals(0, employeeService.getAll().size());
    }

    @Test
    public void deleteEmployeeWithHoliday() throws Exception {
        assertEquals(1, employeeService.getAll().size());
        Employee employee = employeeService.getAll().get(0);

        Holiday holiday = createHoliday(employee, TipConcediu.Co, LocalDate.now());
        holidayService.saveHoliday(holiday);

        employeeService.deleteEmployee(employee);

        assertEquals(0, employeeService.getAll().size());
    }

    @Test
    public void getEmployeeListForCondica() throws Exception {
        assertEquals(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear()), employeeService.getEmployeeListForCondica(LocalDate.now()).size());
    }
}
